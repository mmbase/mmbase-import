/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;
import java.math.*;
import java.util.*;
import java.text.*;
import org.mmbase.util.LocalizedString;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * DataType associated with {@link java.math.BigDecimal}, a NumberDataType. Decimals are naturally
 * restricted on {@link BigDecimal#precision()} and {@link BigDecimal#scale()}.
 *
 * The <em>precision</em> of a decimal can be identified with its <em>length</em>, because it is simply the
 * number of stored digits. Therefore this class <em>does</em>, in contradication to other
 * NumberDataTypes implement {@link LengthDataType}. {@link #getPrecision} and {@link
 * #getMaxLength} are synonymous though.
 *
 * The precision and scale properties correspond with the xsd-tags xsd:precision and xsd:scale,
 * indicating <em>maximal</em> values for those concepts. The can be set with dt:precision
 * (dt:maxLength should work too) and dt:scale.
 *
 * The rounding mode is used, and must be relaxed (since it is default {@link
 * RoundingMode#UNNECESSARY}), if the scale restriction is not enforced. In this case the values can
 * be rounded before validation and storage, but we need to know how precisely this must happen.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class DecimalDataType extends NumberDataType<BigDecimal> implements LengthDataType<BigDecimal> {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logging.getLoggerInstance(DecimalDataType.class);


    protected PrecisionRestriction precisionRestriction  = new PrecisionRestriction();
    protected AbstractLengthDataType.MinRestriction minRestriction  = new AbstractLengthDataType.MinRestriction(this, 1);
    protected ScaleRestriction     scaleRestriction      = new ScaleRestriction();

    private RoundingMode roundingMode = RoundingMode.UNNECESSARY;


    public DecimalDataType(String name) {
        super(name, BigDecimal.class);
        setMin(null, false);
        setMax(null, false);
    }



    // LengthDataType
    public long getLength(Object o) {
        if (o == null) return 0;
        return ((BigDecimal) o).precision();
    }
    // LengthDataType
    public long getMinLength() {
        return minRestriction.getValue();
    }
    // LengthDataType
    public DataType.Restriction<Long> getMinLengthRestriction() {
        return minRestriction;
    }
    // LengthDataType
    public void setMinLength(long value) {
        minRestriction.setValue(value);
    }

    // LengthDataType
    public long getMaxLength() {
        return (long) getPrecision();
    }
    // LengthDataType
    public DataType.Restriction<Long> getMaxLengthRestriction() {
        return precisionRestriction;

    }
    // LengthDataType
    public void setMaxLength(long value) {
        setPrecision((int) value);
    }

    @Override protected BigDecimal castString(Object preCast, Cloud cloud) throws CastException {
        if (preCast == null || "".equals(preCast)) return null;
        Number su = super.castString(preCast, cloud);
        if (su instanceof BigDecimal) {
            return ((BigDecimal) su).round(new MathContext(Integer.MAX_VALUE, roundingMode));
        } else {
            throw new CastException("Not a big decimal " + preCast);
        }
    }


    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public void setRoundingMode(String mode) {
        roundingMode = RoundingMode.valueOf(mode);
    }

    public void setPrecision(int p) {
        precisionRestriction.setValue((long) p);
    }

    public int getPrecision() {
        return precisionRestriction.getValue().intValue();
    }
    public int getScale() {
        return scaleRestriction.getValue();
    }

    public PrecisionRestriction getPrecisionRestriction() {
        return precisionRestriction;
    }
    public ScaleRestriction getScaleRestriction() {
        return scaleRestriction;
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof DecimalDataType) {
            DecimalDataType compOrigin = (DecimalDataType) origin;
            precisionRestriction.inherit(compOrigin.precisionRestriction);
            scaleRestriction.inherit(compOrigin.scaleRestriction);

        }
    }
    @Override protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof DecimalDataType) {
            DecimalDataType dataType = (DecimalDataType) origin;
            precisionRestriction  = new PrecisionRestriction(dataType.precisionRestriction);
            scaleRestriction  = new ScaleRestriction(dataType.scaleRestriction);
        }
    }

    @Override protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value,  Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value, node, field);
        if (log.isDebugEnabled()) {
            log.debug("Validating for " + field + " " + castValue.getClass() + " " + castValue);
        }
        errors = precisionRestriction.validate(errors, castValue, node, field);
        errors = scaleRestriction.validate(errors, castValue, node, field);
        return errors;
    }



    public class PrecisionRestriction extends AbstractRestriction<Long> {

        private static final long serialVersionUID = 1L;

        PrecisionRestriction(PrecisionRestriction source) {
            super(source);
        }
        PrecisionRestriction() {
            super("precision", 128L);
        }
        @Override protected boolean simpleValid(Object v, Node node, Field field) {
            if ((v == null) || (getValue() == null)) return true;
            BigDecimal compare = (BigDecimal) v;
            long max = getValue();
            int scale = DecimalDataType.this.getScale();
            RoundingMode rm = DecimalDataType.this.getRoundingMode();
            if (rm == RoundingMode.UNNECESSARY) rm = RoundingMode.UP;
            compare = compare.setScale(scale, rm);
            if (log.isDebugEnabled()) {
                log.debug("Checking " + compare  + " " + compare.precision() + " <= " +max + " scale; " + scale);
            }
            return compare.precision() <= max;
        }
    }

    public class ScaleRestriction extends AbstractRestriction<Integer> {
        ScaleRestriction(ScaleRestriction source) {
            super(source);
        }
        ScaleRestriction() {
            super("scale", 34);
        }

        @Override protected boolean simpleValid(Object v, Node node, Field field) {
            if ((v == null) || (getValue() == null)) return true;
            BigDecimal compare = (BigDecimal) v;
            int max = getValue();
            return compare.scale() <= max;
        }
    }



}

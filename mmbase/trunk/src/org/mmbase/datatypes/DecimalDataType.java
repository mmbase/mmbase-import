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
 * DataType associated with {@link java.math.BigDecimal}, a NumberDataType, but provides getMin and getMax as int.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DecimalDataType.java,v 1.1 2008-12-01 17:24:14 michiel Exp $
 * @since MMBase-1.9.1
 */
public class DecimalDataType extends NumberDataType<BigDecimal> implements LengthDataType<BigDecimal> {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logging.getLoggerInstance(DecimalDataType.class);


    protected PrecisionRestriction precisionRestriction  = new PrecisionRestriction();
    protected AbstractLengthDataType.MinRestriction minRestriction  = new AbstractLengthDataType.MinRestriction(this, 1);
    protected ScaleRestriction     scaleRestriction      = new ScaleRestriction();

    private RoundingMode roundingMode = RoundingMode.UNNECESSARY;

    /**
     * @param primitive indicate if a primitive type should be used
     */
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

    @Override protected Number castString(Object preCast, Cloud cloud) throws CastException {
        if (preCast == null || "".equals(preCast)) return null;
        if (preCast instanceof Number) {
            return (Number) preCast;
        } else if (preCast instanceof CharSequence) {
            try {
                BigDecimal dec = new BigDecimal("" + preCast, new MathContext(getPrecision(), roundingMode));
                return dec;
            } catch (NumberFormatException nfe) {
                throw new CastException(nfe);
            }
        } else {
            return Casting.toDecimal(preCast);
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
        errors = precisionRestriction.validate(errors, castValue, node, field);
        errors = scaleRestriction.validate(errors, castValue, node, field);
        return errors;
    }



    public class PrecisionRestriction extends AbstractRestriction<Long> {
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
            if (DecimalDataType.this.getRoundingMode() != RoundingMode.UNNECESSARY) {
                if (compare.scale() > DecimalDataType.this.getScale()) {
                    // will be rounded anyway, so we can allow for more precision left of the decimal
                    max += (compare.scale() - DecimalDataType.this.getScale());
                }
            }
            return compare.precision() < max;
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
            return compare.scale() < max;
        }
    }



}

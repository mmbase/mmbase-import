package nl.vpro.mmbase.applications.nodegraph;

import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;

import org.mmbase.util.*;
import org.mmbase.module.database.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.ClusterBuilder;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.builders.DayMarkers;
import org.mmbase.util.logging.*;

import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

public class NodeDatasetProducer implements DatasetProducer, Serializable {

    private static Logger log = Logging.getLoggerInstance(NodeDatasetProducer.class.getName());

    private static final Hashtable cache = new Hashtable();

    private static final long day   =  1;
    private static final long week  =  7 * day;
    private static final long month = 30 * day;
    private static final long year  = 52 * week;

    private MMBase          mmbase      = MMBase.getMMBase();
    private ClusterBuilder  cluster     = mmbase.getClusterBuilder();
    private DayMarkers      daymarks    = (DayMarkers)mmbase.getMMObject("daymarks");
    private TypeDef         typedef     = mmbase.TypeDef;

    public NodeDatasetProducer() { 

    }

    private DefaultCategoryDataset collect(int buildernr, long begin, long end, long step) { 
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        String buildername = typedef.getValue(buildernr);

        if(buildername != null && !buildername.equals("")) {
            // log.info("buildernr("+buildernr+"), buildername("+buildername+"), begin("+begin+"), end("+end+"), step("+step+")");
            //
            int s = 0;
            MultiConnection con = null;
            Statement stmt=null;
            String query = null;
              
            for(long x = begin; x > end; x = x - step, s = s + 1 ) { 

                int daym1 = daymarks.getDayCountAge((int)x);
                int daym2 = daymarks.getDayCountAge((int)(x-step));

                // count = numberofnodes between (daym1,daym2);
                query = new String("select count(*) from "+mmbase.baseName+"_"+buildername+" where number between "+daym1+" and "+daym2);
                int score = 0;
                if(cache.containsKey(query)) { 
                    score = ((Integer)cache.get(query)).intValue();
                } else { 
                    try { 
                        if(con == null) 
                            con = mmbase.getConnection();
                        stmt=con.createStatement();
                        ResultSet rs=stmt.executeQuery(query);
                        if(rs.next()) 
                            score = rs.getInt(1);
                        cache.put(query, new Integer(score));
                    } catch(Exception e) { 
                        log.error("Error wile executing query("+query+"): " +e);
                        cache.put(query, new Integer(0));
                    } 
                }
                // log.info("query("+query+"): score("+score+")");
                
                result.addValue(score,new Integer(s),new Integer(s));
                // log.info("query("+query+"): score("+new Integer(score)+"), x("+new Long(x)+"), y("+buildername+")");
            }
            mmbase.closeConnection(con,stmt);
        }
        return result;
    }

    private DefaultCategoryDataset prepareDataset(int buildernr, String timeframe) { 

        long now = (DateSupport.currentTimeMillis()/1000) / 60 / 60 / 24;
        long before = 0;
        long stepsize = 0;

        if(timeframe.equals("week")) { 
            before = week;
            stepsize = day;
        } else if(timeframe.equals("month")) { 
            before = month;
            stepsize = day; 
        } else if(timeframe.equals("year")) {
            before = 6 * year;
            stepsize = month;
        } else 
            log.error("this timeframe("+timeframe+") is not valid (week/month/year)");
       
        DefaultCategoryDataset dataset = collect(buildernr, before, 0, stepsize); 
        return dataset;
    }

    public synchronized Object produceDataset(Map params) throws DatasetProduceException {
        DefaultCategoryDataset data = null;
        int buildernr       = 0;
        String timeframe    = null;
        try { 
            buildernr = Integer.parseInt((String)params.get("builder"));
            timeframe = (String)params.get("timeframe");
            log.info("buildernr("+buildernr+"), timeframe("+timeframe+")");
            data = prepareDataset(buildernr,timeframe);
        } catch(NumberFormatException e) { 
            log.error("param(pollquestion), value("+params.get("pollquestion")+") is not a valid number!: " + e);
        }
        return data;
    }

    public boolean hasExpired(java.util.Map map,java.util.Date date) {
        return true;
    }

    public String getProducerId() {
        return NodeDatasetProducer.class.getName();
    }

    private class NodeScoreClass implements CompareInterface {
        public String   title = null;
        public Integer  score = null;

        public NodeScoreClass( String title, Integer score ) { 
            this.title = title;
            this.score = score;
        }

        public int compare(Object o1, Object o2) { 
            if(o1 instanceof NodeScoreClass && o2 instanceof NodeScoreClass) { 
                return ((NodeScoreClass)o2).score.intValue() - ((NodeScoreClass)o1).score.intValue();
            } else { 
                log.error("Cannot compare, object not of type "+this.getClass().getName()+", object("+o1+","+o2+")");
                return 0;
            }
        }
    }
}



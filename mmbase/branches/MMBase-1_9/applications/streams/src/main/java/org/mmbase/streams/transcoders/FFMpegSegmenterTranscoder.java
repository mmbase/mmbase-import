package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.Format;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.*;
import org.mmbase.util.transformers.Asciifier;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.Identifier;
import org.mmbase.util.transformers.StringTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Segmenter transcoder that uses version 3 FFmpeg to create ts segments and its m3u8 play-list file.
 * It overrides ffmpeg's `-segment_list` and `-segment_list_entry_prefix` arguments and uses
 * filenames and paths provided by the streams application.
 * Use parameter `httpPrefix` in createcaches.xml to set host and basefilepath. Default is: `http://localhost:8080/files/`.
 * In createcaches.xml for example:
 *  &lt;transcoder
 *      label="playlist-sd"
 *      mimetype="video/*" in="v4" id="v10"&gt;
 *    &lt;class name="org.mmbase.streams.transcoders.FFMpegSegmenterTranscoder"&gt;
 *      &lt;param name="httpPrefix"&gt;http://localhost:8080/files/&lt;/param&gt;
 *      &lt;param name="-c:v"&gt;libx264&lt;/param&gt;
 *      &lt;param name="-b:v"&gt;128k&lt;/param&gt;
 *      &lt;param name="format"&gt;m3u8&lt;/param&gt;
 *      &lt;param name="-flags"&gt;-global_header&lt;/param&gt;
 *      &lt;param name="-map"&gt;0&lt;/param&gt;
 *      &lt;param name="-f"&gt;ssegment&lt;/param&gt;
 *      &lt;param name="-segment_time"&gt;10&lt;/param&gt;
 *      &lt;param name="-segment_format"&gt;mpegts&lt;/param&gt;
 *    &lt;/class&gt;
 *    &lt;loganalyzer name="org.mmbase.streams.transcoders.SegmenterAnalyzer" /&gt;
 *  &lt;/transcoder&gt;
 *
 * @author Andr&eacute; van Toly
 * @version $Id: FFMpegSegmenterTranscoder.java 46335 2012-04-16 15:01:43Z andre $
 */
@Settings({"httpPrefix"})
public class FFMpegSegmenterTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(FFMpegSegmenterTranscoder.class);
    private static CharTransformer identifier = new Identifier();
    private static StringTransformer asciifier = new Asciifier();

    String acodec = null;
    String vcodec = null;

    String httpPrefix = "http://localhost:8080/files/";
    public void setHttpPrefix(String h) {
        httpPrefix = h;
    }
    String filePath= "";

    public Codec getCodec() {
        if (vcodec != null) {
            return AnalyzerUtils.libtoCodec(vcodec);
        } else if (acodec != null) {
            return AnalyzerUtils.libtoCodec(acodec);
        } else {
            return null;
        }
    }

    @Override
    protected String getCommand() {
        return "ffmpeg";
    }

    @Override
    protected LoggerWriter getErrorWriter(Logger log) {
        // ffmpeg write also non-errors to stderr, so lets not log on ERROR, but on SERVICE.
        // also pluging an error-detector here.
        return new LoggerWriter(new ChainedLogger(log, new ErrorDetector(Pattern.compile("\\s*Unknown encoder.*"))), Level.SERVICE);
    }

    public FFMpegSegmenterTranscoder() {
        format = Format.M3U8;
    }

    /*
     * Get destination url and filename to make somethings ffmpeg can create a m3u8 and ts segment files with.
     */
    public void init(Node dest) {
        String mt = dest.getStringValue("mimetype");
        if (mt == null || "".equals(mt)) {
            dest.setStringValue("mimetype", "application/x-mpegurl");
        }

        String url = dest.getStringValue("url");
        if (url.length() < 1) {
            log.warn("Still empty url: '" + url+ "' of #" + dest.getNumber());
        } else {
            log.debug("filename: " + url);

            // remove punctuation from url
            String regex = "^((.*/)?([0-9]+\\.?[0-9]*)?\\.)(.*)\\.m3u8";
            Pattern FILE_PATTERN = Pattern.compile(regex);
            Matcher m = FILE_PATTERN.matcher(url);

            StringBuilder fileName = new StringBuilder(url);

            if (m.matches()) {
                if (log.isDebugEnabled()) {
                    log.debug("match 1: " + m.group(1));
                    log.debug("match 2: " + m.group(2));
                    log.debug("match 3: " + m.group(3));
                    log.debug("match 4: " + m.group(4));
                }

                String begin = m.group(1);
                filePath = m.group(2);
                String base = m.group(4);
                String ext = url.substring(url.lastIndexOf('.'), url.length());

                if (log.isDebugEnabled()) {
                    log.debug("begin: " + begin);
                    log.debug("base : " + base);
                    log.debug("ext  : " + ext);
                }

                fileName = new StringBuilder(begin);
                base = asciifier.transform(base);
                fileName.append(identifier.transform(base));
                fileName.append(ext);
            }
            url = fileName.toString();
            dest.setStringValue("url", url);

            if (log.isDebugEnabled()) log.debug("url now: " + url);
        }
    }

    @Override
    protected String[] getArguments() {
        if (! in.getScheme().equals("file")) throw new UnsupportedOperationException();
        if (! out.getScheme().equals("file")) throw new UnsupportedOperationException();

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());

        List<String> args = new ArrayList<String>();

        args.add("-i");
        args.add(inFile.toString());

        String outFilename = outFile.toString();
        args.add("-segment_list");
        args.add(outFilename);   // m3u8 playlist

        // ffmpeg cuts out complete path in filenames it writes in playlist file
        // add correct webserver filePath with -segment_list_entry_prefix
        args.add("-segment_list_entry_prefix");
        args.add(httpPrefix + filePath);

        outFilename = outFilename.substring(0, outFilename.lastIndexOf('.')) + "_%05d.ts";  // template for ts segments
        args.add("-y");
        args.add(outFilename);

        if (log.isDebugEnabled()) {
            log.debug("args: " + args.toString());
            log.debug("output file: " + outFile.toString());
        }

        return args.toArray(new String[args.size()]);
    }

    private static final Pattern PROGRESS = Pattern.compile(".*time remaining.*");

    @Override
    protected LoggerWriter getOutputWriter(final Logger log) {
        LoggerWriter w = new LoggerWriter(log, Level.SERVICE) {
                   @Override
                   public Level getLevel(String line) {
                       if (PROGRESS.matcher(line).matches()) {
                           return Level.DEBUG;
                       }
                       return null;
                   }
               };

        return w;
    }

    @Override
    public FFMpegSegmenterTranscoder clone() {
        return (FFMpegSegmenterTranscoder) super.clone();
    }

}

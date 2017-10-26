package mm.server.work;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import mm.server.utils.Tools;

public class IpRequestDTO {
    private static Pattern HTTP_URI_PATTERN = Pattern.compile(
        "([^/]+)/?\\?(ip)?=([0-9\\.]+)"
    );
    private final String ip;
    private final String dbName;

    IpRequestDTO(String ip, String dbName) {
        this.ip = ip;
        this.dbName = dbName;
    }

    public static IpRequestDTO parseUri(String uri) {
        String ip;
        String dbName;
        Matcher m = HTTP_URI_PATTERN.matcher(uri);
        if (!m.find()) {
            return null;
        }
        dbName = m.group(1);
        ip = m.group(3);

        if (!Tools.isIp(ip)) {
            return null;
        }

        return new IpRequestDTO(ip, dbName);
    }

    public String getIp() {
        return ip;
    }

    public String getDbName() {
        return dbName;
    }
}

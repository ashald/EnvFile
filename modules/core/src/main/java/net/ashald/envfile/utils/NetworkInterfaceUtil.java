package net.ashald.envfile.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkInterfaceUtil {

    private final static String IPv4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

    public static String getIpV4(String name) throws NoSuchElementException {
        List<NetworkInterface> interfaces = getNetworkInterfaceWithIp4();
        List<InetAddress> inetAddresses = null;

        for (NetworkInterface anInterface : interfaces) {
            if (anInterface.getDisplayName().equalsIgnoreCase(name)) {
                inetAddresses = getIpv4Addresses(anInterface);
                break;
            }
        }
        if (inetAddresses == null) {
            throw new NoSuchElementException("Found no Network Interface: " + name);
        }
        if (inetAddresses.size() == 0)
            throw new NoSuchElementException("Found no Ip v4 Address for Network Interface: " + name);

        return inetAddresses.get(0).toString().substring(1);
    }

    public static List<String> getNetworkInterfaceWithIpV4Names() {
        List<NetworkInterface> interfaces = getNetworkInterfaceWithIp4();
        List<String> interfaceNames = new ArrayList<>();
        for (NetworkInterface networkInterface : interfaces) {
            interfaceNames.add(networkInterface.getDisplayName());
        }
        return interfaceNames;
    }

    private static List<NetworkInterface> getNetworkInterfaceWithIp4() {
        List<NetworkInterface> interfaces = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> inter = NetworkInterface.getNetworkInterfaces(); inter.hasMoreElements();) {
                NetworkInterface networkInterface = inter.nextElement();
                List<InetAddress> inetAddresses = getIpv4Addresses(networkInterface);
                if (inetAddresses.size() > 0)
                    interfaces.add(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return interfaces;
    }

    private static List<InetAddress> getIpv4Addresses(NetworkInterface networkInterface) {
        List<InetAddress> networkInterfaces = new ArrayList<>();

        for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
            InetAddress inetAddress = enumIpAddr.nextElement();
            String ip = inetAddress.toString().substring(1);
            Pattern VALID_IPV4_PATTERN = Pattern.compile(IPv4_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = VALID_IPV4_PATTERN.matcher(ip);
            if (matcher.matches() && !ip.equals("127.0.0.1"))
                networkInterfaces.add(inetAddress);
        }
        return networkInterfaces;
    }

}

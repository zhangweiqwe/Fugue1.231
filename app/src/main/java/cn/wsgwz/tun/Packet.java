package cn.wsgwz.tun;

/*
    This file is part of NetGuard.

    NetGuard is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NetGuard is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NetGuard.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015-2017 by Marcel Bokhorst (M66B)
*/

public class Packet {
    public long time;
    public int version;
    public int protocol;//if (!(packet.protocol == 6 /* TCP */ || packet.protocol == 17 /* UDP */))
    public String flags;
    public String saddr;
    public int sport;
    public String daddr;
    public int dport;
    public String data;
    public int uid;
    public boolean allowed;

    public Packet() {
    }

    @Override
    public String toString() {
        return "uid=" + uid + " v" + version + " p" + protocol + " " + daddr + "/" + dport;
    }


    public String toString2() {
        return "Packet{" +
                "time=" + time +
                ", version=" + version +
                ", protocol=" + protocol +
                ", flags='" + flags + '\'' +
                ", saddr='" + saddr + '\'' +
                ", sport=" + sport +
                ", daddr='" + daddr + '\'' +
                ", dport=" + dport +
                ", data='" + data + '\'' +
                ", uid=" + uid +
                ", allowed=" + allowed +
                '}';
    }
}

package it.intelligrate.convertMaxMindRegion;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.IspResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import java.sql.Statement;


public class App 
{
	public static void main( String[] args ) throws GeoIp2Exception, UnknownHostException, SQLException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		File cityDatabase = new File("c:/GeoLite2-City.mmdb");
		File ispDatabase = new File("c:/GeoLite2-City.mmdb");
		Connection conn = null;
		InetAddress ipAddress = null;
		CityResponse cityResponse = null;
		IspResponse ispResponse = null;
		Country country = null;
		Subdivision subdivision = null;
		City city = null;
		DatabaseReader cityReader = null;
		DatabaseReader ispReader = null;
		String organization = null;
		Statement stmt = null;
		ResultSet rs = null;
		String ip=null;
		String cc=null;
		String sCountry=null;
		String sRegion=null;
		String sCity=null;
		String sIsp=null;

		conn = DriverManager.getConnection("jdbc:mysql://rake-mps/RAKE_PROFILES?" +
				"user=rake&password=rake");

		stmt = conn.createStatement();



		try {
			cityReader = new DatabaseReader.Builder(cityDatabase).build();
			ispReader = new DatabaseReader.Builder(ispDatabase).build();
			rs = stmt.executeQuery("SELECT CONTOCORRENTE,IP FROM CC_IP");
			while (rs.next()) {
				cc=rs.getString(1);
				ip=rs.getString(2);

				System.out.println("ipAddess=" + ip);
				ipAddress = InetAddress.getByName(ip);
				try {
					cityResponse = cityReader.city(ipAddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cityResponse != null) {
						country = cityResponse.getCountry();
						if (country.getIsoCode()==null) {
							sCountry="N/A";
						} else {
							sCountry=country.getIsoCode();
						}

						subdivision = cityResponse.getLeastSpecificSubdivision();
						if (subdivision.getIsoCode()==null) {
							sRegion="N/A";
						} else {
							sRegion=subdivision.getIsoCode();
						}

						city = cityResponse.getCity();
						if (city.getName()==null) {
							sCity="N/A";
						} else {
							sCity=city.getName();
						}

						cityResponse = null;
					}
					/*
						ispResponse = ispReader.isp(ipAddress);
						if (ispResponse != null) {

							organization = ispResponse.getOrganization();
			   				if (city.getName()==null) {
			   					sIsp="N/A";
			   				} else {
			   					sIsp=city.getName();
			   				}
							ispResponse=null;
						}
					 */
					country=null;
					subdivision=null;
					city=null;
					organization=null;
					System.out.println(cc+", "+sCountry+", "+sRegion+", "+sCity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

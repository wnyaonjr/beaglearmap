/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file is part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package org.mixare.reality;

import org.mixare.render.MixVector;

import android.location.Location;

/**
 * The container of the attributes of a marker which are related to location
 */
public class PhysicalPlace {
	
	/** The location in latitude of the marker **/
	double latitude;
	
	/** The location in longitude of the marker**/
	double longitude;
	
	/** The elevation/altitude of the marker **/
	double altitude;
	
	/** The distance of the marker from the current location **/
	private double distance;
	
	/** The bearing of the marker with respects to the user's current location and to the true north **/
	double bearing;
	
	public PhysicalPlace() {}

	public PhysicalPlace(PhysicalPlace pl) {
		this.setTo(pl.latitude, pl.longitude, pl.altitude);
	}

	public PhysicalPlace(double latitude, double longitude, double altitude) {
		this.setTo(latitude, longitude, altitude);
	}

	/** Sets the latitude, longitude, and altitude **/
	public void setTo(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	/** Sets the latitude, longitude, altitude, distance, and bearing **/
	public void setTo(PhysicalPlace pl) {
		this.latitude = pl.latitude;
		this.longitude = pl.longitude;
		this.altitude = pl.altitude;
		this.distance = pl.distance;
		this.bearing = pl.bearing;
	}

	/** @return the latitude */
	public double getLatitude() {
		return latitude;
	}
	
	/** @param latitude the latitude to set */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/** @return the longitude */
	public double getLongitude() {
		return longitude;
	}

	/** @param longitude the longitude to set */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/** @return the altitude */
	public double getAltitude() {
		return altitude;
	}

	/** @param altitude the altitude to set */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
		
	/** @return the distance */
	public double getDistance() {
		return distance;
	}

	/** @param distance the distance to set */
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	/** @return the bearing */
	public double getBearing() {
		return bearing;
	}

	/** @param bearing the bearing to set */
	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	/** Computes for the Location **/
	public static void calcDestination(double lat1Deg, double lon1Deg, double bear, double d, PhysicalPlace dest) {
		
		double brng = Math.toRadians(bear);
		double lat1 = Math.toRadians(lat1Deg);
		double lon1 = Math.toRadians(lon1Deg);
		double R = 6371.0 * 1000.0; 

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R)
				+ Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));
		double lon2 = lon1
				+ Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
						Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

		dest.setLatitude(Math.toDegrees(lat2));
		dest.setLongitude(Math.toDegrees(lon2));
	}

	/** Converts the location values (longitude,latitude) to vectors **/
	public static void convLocToVec(Location org, PhysicalPlace gp,
			MixVector v) {
		float[] z = new float[1];
		z[0] = 0;
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), gp
				.getLatitude(), org.getLongitude(), z);
		
		float[] x = new float[1];
		Location.distanceBetween(org.getLatitude(), org.getLongitude(), org
				.getLatitude(), gp.getLongitude(), x);
		
		double y = gp.getAltitude() - org.getAltitude();
		
		if (org.getLatitude() < gp.getLatitude())
			z[0] *= -1;
		if (org.getLongitude() > gp.getLongitude())
			x[0] *= -1;
		v.set(x[0], (float) y, z[0]);
	}

	/** Converts vector values to Location values (longitude,latitude) **/
	public static void convertVecToLoc(MixVector v, Location org, Location gp) {
		double brngNS = 0, brngEW = 90;
		if (v.z > 0)
			brngNS = 180;
		if (v.x < 0)
			brngEW = 270;

		PhysicalPlace tmp1Loc = new PhysicalPlace();
		PhysicalPlace tmp2Loc = new PhysicalPlace();
		PhysicalPlace.calcDestination(org.getLatitude(), org.getLongitude(), brngNS,
				Math.abs(v.z), tmp1Loc);
		PhysicalPlace.calcDestination(tmp1Loc.getLatitude(), tmp1Loc.getLongitude(),
				brngEW, Math.abs(v.x), tmp2Loc);

		gp.setLatitude(tmp2Loc.getLatitude());
		gp.setLongitude(tmp2Loc.getLongitude());
		gp.setAltitude(org.getAltitude() + v.y);
	}
}

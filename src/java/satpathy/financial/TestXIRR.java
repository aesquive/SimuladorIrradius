/*
 *  TestXIRR.java
 *  Copyright (C) 2005 Gautam Satpathy
 *  gautam@satpathy.in
 *  www.satpathy.in
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package satpathy.financial;

/*
 *  Imports
 */
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author : gsatpath
 * @version : 1.0.0 Date: Oct 20, 2005, Time: 4:39:25 AM
 */
public class TestXIRR {

	/**
	 *
	 *  @param args
	 */
	public static void main( String[] args ) {
		log( "Testing XIRR..." ) ;

//		GregorianCalendar dateStart = new GregorianCalendar( 1899, 11, 30 ) ;
		GregorianCalendar dateEnd = new GregorianCalendar( 2005, 9, 20 ) ;
        int daysBetween = XIRRData.getExcelDateValue( dateEnd ) ;
		log( "Days Between = " + daysBetween ) ;

//		"Let us assume that the cells A1:A5 contain the numbers -6000, "
//		"2134, 1422, 1933, and 1422, and the cells B1:B5 contain the "
//		"dates \"1999-01-15\", \"1999-04-04\", \"1999-05-09\", "
//		"\"2000-03-12\", and \"2000-05-1\". Then\n"
//		"XIRR(A1:A5,B1:B5) returns 0.224838. "
		
                double[] valores=new double[]{-30000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                                            10000,1303.5709,1564.9033,1734.8126,1824.5757,1818.2785,11745.6322,1550.9065,1264.5285,894.8276,522.6075,261.2753,41449.1751}; 
                double[]    dates   = new double[valores.length];
        Calendar instance = Calendar.getInstance();
        Date time = instance.getTime();
        time.setMonth(time.getMonth()-2);    
        time.setDate(1);
        System.out.println(time);
        for(int t=0;t<valores.length;t++){
            Calendar instance1 = Calendar.getInstance();
            instance1.setTime(time);
            dates[t]=XIRRData.getExcelDateValue(instance1);
            time.setMonth(time.getMonth()+1);
        }
		XIRRData data       = new XIRRData( valores.length, 0.5, valores, dates ) ;
		double xirrValue = XIRR.xirr( data ) ;
        log( "XIRR = " + xirrValue ) ;

		log( "XIRR Test Completed..." ) ;
	}


	/**
	 *
	 * @param message
	 */
	public static void log( String message ) {
		System.out.println( message ) ;
	}

}   /*  End of the TestXIRR class. */

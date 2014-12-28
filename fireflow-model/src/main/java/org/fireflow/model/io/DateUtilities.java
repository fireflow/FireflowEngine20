/*--

 Copyright (C) 2002-2003 Anthony Eden.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

 3. The names "OBE" and "Open Business Engine" must not be used to
    endorse or promote products derived from this software without prior
    written permission.  For written permission, please contact
    me@anthonyeden.com.

 4. Products derived from this software may not be called "OBE" or
    "Open Business Engine", nor may "OBE" or "Open Business Engine"
    appear in their name, without prior written permission from
    Anthony Eden (me@anthonyeden.com).

 In addition, I request (but do not require) that you include in the
 end-user documentation provided with the redistribution and/or in the
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by
      Anthony Eden (http://www.anthonyeden.com/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR(S) BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 For more information on OBE, please see <http://www.openbusinessengine.org/>.

 */

package org.fireflow.model.io;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Date utilities.
 * @author Anthony Eden
 *
 */
public class DateUtilities{

    private static final DateUtilities dateUtilities = new DateUtilities();
    private List<SimpleDateFormat> formats;

    /**
     * Construct a new DateUtilities class.
     */
    private DateUtilities(){
        resetFormats();
    }

    /** 
     * Reset the supported formats to the default set. 
     */
    public void resetFormats(){
        formats = new ArrayList<SimpleDateFormat>();

        // ISO formats
        formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        formats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"));

        // XPDL examples format
        formats.add(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a"));

        // alternative formats
        formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /** 
     * Get an instance of the DateUtilities class.
     * @return A DateUtilities instance
     */
    public static DateUtilities getInstance(){
        return dateUtilities;
    }

    /** 
     * Parse the specified date String.
     * @param dateString The date String
     * @return The Date object
     * @throws ParseException If the date format is not supported
     */
    public Date parse(String dateString) throws ParseException{
        Iterator<SimpleDateFormat> iter = formats.iterator();
        while(iter.hasNext()){
            try{
                return ((DateFormat)iter.next()).parse(dateString);
            } catch(ParseException e){
                // do nothing
            }
        }
        throw new ParseException("Unsupported date format", -1);
    }

    /** 
     * Return a List of date formats to try.
     * @return A List of DateFormat objects
     */
    public List<SimpleDateFormat> getFormats(){
        return formats;
    }

}

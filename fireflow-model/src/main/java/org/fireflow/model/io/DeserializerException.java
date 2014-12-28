

package org.fireflow.model.io;


/**
 * 
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class DeserializerException  extends Exception{

    /** 
     * Construct a new FPDLParserException. 
     */
    public DeserializerException(){
        super();
    }

    /** 
     * Construct a new FPDLParserException with the specified message.
     * @param message The error message
     */
    public DeserializerException(String message){
        super(message);
    }

    /** 
     * Construct a new FPDLParserException with the specified nested error.
     * @param t The nested error
     */
    public DeserializerException(Throwable t){
        super(t);
    }

    /** 
     * Construct a new FPDLParserException with the specified error message<br>
     * and nested exception.
     * @param message The error message
     * @param t The nested error
     */
    public DeserializerException(String message, Throwable t){
        super(message, t);
    }

}

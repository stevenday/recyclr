package uk.co.gifteconomy.model.yahoo.geocoder;

/**
 * An enumeration of the error codes returned by the Yahoo Places Geocoder API.
 * 
 * @author steve
 *
 */
public enum ErrorCodes {
    NO_ERROR("0"),
    FEATURE_NOT_SUPPORTED("1"),
    NO_INPUT_PARAMETERS("100"),
    ADDRESS_DATA_NOT_UTF8("102"),
    INSUFFICIENT_DATA("103"),
    UNKNOWN_LANGUAGE("104"),
    NO_COUNTRY_DETECTED("105"),
    COUNTRY_NOT_SUPPORTED("106"),
    INTERNAL_PROBLEM("10NN");
    
    private String code;
    
    private ErrorCodes(String code) {
    	this.code = code;
    }

	public String getCode() {
		return this.code;
	}


}

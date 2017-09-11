package com.pier.business.validation;

public class FullNameValidator {
	
	public static boolean validateFirstName( String firstName )	   {
	      return firstName.matches( "[A-Z][a-zA-Z]*" );
	   } 
	   
	   public static boolean validateLastName( String lastName )
	   {
	      return lastName.matches( "[a-zA-Z]+([ '-][a-zA-Z]+)*" );
	   } // end method validateLastName

}

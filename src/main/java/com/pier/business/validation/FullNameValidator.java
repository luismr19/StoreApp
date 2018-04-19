package com.pier.business.validation;

public class FullNameValidator {
	
	public static boolean validateFirstName( String firstName )	   {
	      return firstName.matches( "[A-ZáéíóúÁÉÍÓÚ][a-zA-ZáéíóúÁÉÍÓÚ]*" );
	   }	
	   
	   public static boolean validateLastName( String lastName )
	   {
	      return lastName.matches( "[a-zA-ZáéíóúÁÉÍÓÚ]+([ '-][a-zA-ZáéíóúÁÉÍÓÚ]+)*" );
	   } // end method validateLastName

}

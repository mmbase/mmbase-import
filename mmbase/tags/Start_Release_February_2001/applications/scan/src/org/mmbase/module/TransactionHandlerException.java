	package org.mmbase.module;
	
	/**
	 * transactionHandler exception
	 */
	 
	public class TransactionHandlerException extends Exception {
		String code = "";
		String fieldId = "";
		String fieldOperator = "";
		String objectOperator = "";
		String objectId = "";
		String transactionOperator = "";
		String transactionId = "";
		String exceptionPage = "";

		public TransactionHandlerException(String s) { 
			super(s); 
		}
	}
	

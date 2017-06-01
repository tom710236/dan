package com.example.motoapp;

public class NewItem {
	 
	 private String FormNo;
	 private String CaseID;
	 private String Status;

	public NewItem(String FormNo, String CaseID, String Status) {
		this.FormNo = FormNo;
		this.Status = Status;
		this.CaseID = CaseID;
	}

	 public String getFormNo(){
	  return FormNo;
	 }
	 
	 public String getStatus(){
	  return Status;
	 }
	 public String getCaseID(){
		  return CaseID;
		 }
}
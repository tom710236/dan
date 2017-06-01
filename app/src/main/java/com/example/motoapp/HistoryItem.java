package com.example.motoapp;

public class HistoryItem {
	 
	 private String FormNo;
	 private String Status;
	 private String SDate;
	 
	 public HistoryItem(String FormNo,String Status,String SDate) {
	  this.FormNo=FormNo;
	  this.Status = Status;
	  this.SDate = SDate;
	 }
	 
	 public String getFormNo(){
	  return FormNo;
	 }
	 
	 public String getStatus(){
	  return Status;
	 }

	 public String getSDate(){
		  return SDate;
		 }
}
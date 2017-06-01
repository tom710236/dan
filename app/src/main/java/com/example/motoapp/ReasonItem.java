package com.example.motoapp;

public class ReasonItem {
	 
	 private int Index;
	 private String No;
	 private String Reason;
	 
	 public ReasonItem(int Index,String No,String Reason) {
	  this.Index=Index;
	  this.No = No;
	  this.Reason = Reason;
	 }
	 
	 public int getIndex(){
	  return Index;
	 }
	 
	 public String getNo(){
	  return No;
	 }

	 public String getReason(){
		  return Reason;
		 }
}
package com.example.motoapp;


public class ClsDropDownStation {

	 private String Key = "";
	 private String Value = "";
	 private String StationType = "";

	public ClsDropDownStation(String _Key, String _Value, String _Type) {
		Key = _Key;
		Value = _Value;
		StationType = _Type;
	}

	 @Override
	 public String toString() {
	  return Value;
	 }

	 public String GetID() {
	  return Key;
	 }

	 public String GetValue() {
	  return Value;
	 }
	 
	 public String GetStationType() {
		  return StationType;
		 }
	}

package com.example.motoapp;


public class ClsDropDownItem {

	 private String Key = "";
	 private String Value = "";


	public ClsDropDownItem(String _Key, String _Value) {
		Key = _Key;
		Value = _Value;
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
	}

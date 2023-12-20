//package com.example.demo.entity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//@Entity
////↓「name="xxx"」の「xxx」の部分に模倣したいテーブル名を書く
//@Table(name = "dome_user")
//public class Dome_user {
//
//	//主キーには「@Id」を設定する！
//	@Id
//	//カラム名(列名)を書く。
//	@Column(name = "user_id")
//	private int userid;
//
//	@Column(name = "year_class")
//	private String year_class;
//
//	@Column(name = "user_name")
//	private String user_name;
//
//	@Column(name = "del_flg")
//	private int del_flg;
//
//	public final int getUserid() {
//		return userid;
//	}
//
//	public final void setUserid(int userid) {
//		this.userid = userid;
//	}
//
//	public final String getYear_class() {
//		return year_class;
//	}
//
//	public final void setYear_class(String year_class) {
//		this.year_class = year_class;
//	}
//
//	public final String getUser_name() {
//		return user_name;
//	}
//
//	public final void setUser_name(String user_name) {
//		this.user_name = user_name;
//	}
//
//	public final int getDel_flg() {
//		return del_flg;
//	}
//
//	public final void setDel_flg(int del_flg) {
//		this.del_flg = del_flg;
//	}
//
//}
package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
//↓「name="xxx"」の「xxx」の部分に模倣したいテーブル名を書く
@Table(name = "dome_reservation", uniqueConstraints = {
		@UniqueConstraint(name = "user_id", columnNames = { "user_id" }) }) // FKにUNIQUE制約追加)
public class Dome_reservation {

	//主キーには「@Id」を設定する！
	@Id
	//カラム名(列名)を書く。
	@Column(name = "reservation_id")
	private int reservation_id;

	@Column(name = "date")
	private String date;

	@Column(name = "court")
	private String court;

	@Column(name = "versus")
	private int versus;

	@Column(name = "user_id")
	private int user_id;

	@Column(name = "del_flg")
	private int del_flg;

	//	@OneToOne
	//	@JoinColumn(name = "user_id")
	//	private Dome_user Dome_user;

	public final int getReservation_id() {
		return reservation_id;
	}

	public final void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}

	public final String getDate() {
		return date;
	}

	public final void setDate(String date) {
		this.date = date;
	}

	public final String getCourt() {
		return court;
	}

	public final void setCourt(String court) {
		this.court = court;
	}

	public final int getVersus() {
		return versus;
	}

	public final void setVersus(int versus) {
		this.versus = versus;
	}

	public final int getUser_id() {
		return user_id;
	}

	public final void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public final int getDel_flg() {
		return del_flg;
	}

	public final void setDel_flg(int del_flg) {
		this.del_flg = del_flg;
	}

}
package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
//↓「name="xxx"」の「xxx」の部分に模倣したいテーブル名を書く
@Table(name = "dome_versus")
public class Dome_versus {

	//主キーには「@Id」を設定する！
	@Id
	//カラム名(列名)を書く。
	@Column(name = "versus_id")
	private int versus_id;

	@Column(name = "reservation_id")
	private int reservation_id;

	@Column(name = "versus_class")
	private String versus_class;

	@Column(name = "del_flg")
	private int del_flg;

	public final int getVersus_id() {
		return versus_id;
	}

	public final void setVersus_id(int versus_id) {
		this.versus_id = versus_id;
	}

	public final int getReservation_id() {
		return reservation_id;
	}

	public final void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}

	public final String getVersus_class() {
		return versus_class;
	}

	public final void setVersus_class(String versus_class) {
		this.versus_class = versus_class;
	}

	public final int getDel_flg() {
		return del_flg;
	}

	public final void setDel_flg(int del_flg) {
		this.del_flg = del_flg;
	}

}
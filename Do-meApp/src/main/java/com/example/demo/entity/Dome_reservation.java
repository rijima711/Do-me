package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

	//	@OneToOne
	//	@JoinColumn(name = "user_id")
	//	private Dome_user Dome_user;

}
package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
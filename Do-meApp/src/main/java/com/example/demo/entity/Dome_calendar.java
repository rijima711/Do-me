package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
//↓「name="xxx"」の「xxx」の部分に模倣したいテーブル名を書く
@Table(name = "dome_calendar")
public class Dome_calendar {

	//主キーには「@Id」を設定する！
	@Id
	//カラム名(列名)を書く。
	@Column(name = "calendar_id")
	private int calendar_id;

	@Column(name = "title")
	private String title;

	@Column(name = "start")
	private String start;

	public final int getCalendar_id() {
		return calendar_id;
	}

	public final void setCalendar_id(int calendar_id) {
		this.calendar_id = calendar_id;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getStart() {
		return start;
	}

	public final void setStart(String start) {
		this.start = start;
	}

}
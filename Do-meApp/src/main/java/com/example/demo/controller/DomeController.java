package com.example.demo.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Dome_calendar;
import com.example.demo.entity.Dome_reservation;
import com.example.demo.entity.Dome_versus;
import com.example.demo.repository.Dome_calendarRepository;
import com.example.demo.repository.Dome_reservationRepository;
import com.example.demo.repository.Dome_versusRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DomeController {
	@Autowired
	Dome_reservationRepository dome_reservationRepository;
	@Autowired
	Dome_versusRepository dome_versusRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	Dome_calendarRepository dome_calendar;

	//ログイン機能
	@RequestMapping(path = "/slogin", method = RequestMethod.GET)
	public String slogin() throws IOException {
		return "slogin";
	}

	@RequestMapping(path = "/slogin", method = RequestMethod.POST)
	public String slogin(String user_id, String administrator, RedirectAttributes redirectAttributes,
			HttpSession session)
			throws IOException {
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM dome_user WHERE user_id = ?",
				user_id);

		if (administrator != null) {
			return "redirect:/administrator";
		}

		if (!CollectionUtils.isEmpty(resultList)) {
			String user_name = (String) resultList.get(0).get("user_name");
			String year_class = (String) resultList.get(0).get("year_class");

			// セッションに必要な情報を保存
			session.setAttribute("user_id", user_id);
			session.setAttribute("user_name", user_name);
			session.setAttribute("year_class", year_class);

			return "redirect:/mainmenu";
		} else {
			return "redirect:/slogin";
		}
	}

	//ログアウト機能
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		// ログアウトの処理を実装（セッションのクリアなど）
		// 例えば、セッションをクリアする場合
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		// ログアウト後のリダイレクト先を指定
		return "redirect:/slogin";
	}

	//対戦予約表示機能
	@GetMapping("/versus_show")
	public String versus_show(String user_name, Model model, HttpSession session) {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		List<Map<String, Object>> resultList = null;
		resultList = jdbcTemplate.queryForList(
				"select a.date,a.court,c.year_class,b.versus_class from dome_reservation a inner join dome_versus b on a.reservation_id = b.reservation_id inner join dome_user c on a.user_id = c.user_id;");
		model.addAttribute("resultList", resultList);

		return "versus_show";
	}

	@RequestMapping(path = "/mainmenu", method = RequestMethod.GET)
	public String mainmenu(Model model, HttpSession session) throws IOException {
		// セッションからユーザー情報を取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		return "mainmenu";
	}

	@RequestMapping(path = "/mainmenu", method = RequestMethod.POST)
	public String mainmenu(String yoyaku, String joukyou, String taisen, String taikai)
			throws IOException {
		if (yoyaku != null) {
			return "redirect:/yoyaku";
		} else if (joukyou != null) {
			return "redirect:/joukyou";
		} else if (taisen != null) {
			return "redirect:/taisen";
		} else if (taikai != null) {
			return "redirect:/taikai";
		}
		return "mainmenu";

	}

	//予約画面
	@RequestMapping(path = "/yoyaku", method = RequestMethod.GET)
	public String yoyaku(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "yoyaku";
	}

	@RequestMapping(path = "/yoyaku", method = RequestMethod.POST)
	public String yoyaku(String court, String date, String versus, HttpSession session)
			throws IOException {
		String year_class = (String) session.getAttribute("year_class");
		String title = year_class + "・" + court + "コート";

		String user_id = (String) session.getAttribute("user_id");
		Dome_reservation myReservation = new Dome_reservation();

		// 重複チェック
		List<Dome_reservation> reservationCheck = dome_reservationRepository.findByDateAndCourt(date, court);
		if (!reservationCheck.isEmpty()) {
			return "redirect:/yoyaku_error"; // 重複がある場合の処理
		}

		//予約テーブルに保存
		if (versus != null) {
			myReservation.setDate(date);
			myReservation.setCourt(court);
			myReservation.setVersus(Integer.parseInt(versus));
			myReservation.setUser_id(Integer.parseInt(user_id));
			dome_reservationRepository.save(myReservation);

		} else {
			myReservation.setDate(date);
			myReservation.setCourt(court);
			myReservation.setUser_id(Integer.parseInt(user_id));
			dome_reservationRepository.save(myReservation);
		}
		calendar(title, date);

		return "redirect:/yoyaku_done";

	}

	////予約状況確認のカレンダーテーブルに保存
	//@RequestMapping(path = "/calendar", method = RequestMethod.GET)
	public void calendar(String title, String start) throws IOException {
		//予約状況確認のカレンダーテーブルに保存
		Dome_calendar myCalendar = new Dome_calendar();

		myCalendar.setTitle(title);
		myCalendar.setStart(start);
		dome_calendar.save(myCalendar);
		return;

	}

	//予約完了
	@RequestMapping(path = "/yoyaku_done", method = RequestMethod.GET)
	public String yoyaku_done(Model model, String user_name, HttpSession session) throws IOException {

		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "yoyaku_done";

	}

	@RequestMapping(path = "/yoyaku_done", method = RequestMethod.POST)
	public String yoyaku_done(RedirectAttributes redirectAttributes, String kanryou) throws IOException {

		return "redirect:/mainmenu";

	}

	//予約状況確認画面
	@RequestMapping(path = "/joukyou", method = RequestMethod.GET)
	public String joukyou(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		return "joukyou";
	}

	@RequestMapping(path = "/joukyou", method = RequestMethod.POST)
	public String joukyou() throws IOException {

		return "joukyou";
	}

	//対戦予約画面
	@RequestMapping(path = "/taisen", method = RequestMethod.GET)
	public String taisen(Model model, String user_name, HttpSession session) throws IOException {

		List<Map<String, Object>> resultList = null;
		resultList = jdbcTemplate.queryForList(
				"SELECT a.reservation_id, a.date, a.court, b.user_id, b.year_class, b.user_name FROM dome_reservation a INNER JOIN dome_user b ON a.user_id = b.user_id WHERE a.versus = 1 AND NOT EXISTS ( SELECT 1 FROM dome_versus c WHERE c.reservation_id = a.reservation_id );");
		model.addAttribute("resultList", resultList);

		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "taisen";
	}

	@RequestMapping(path = "/taisen", method = RequestMethod.POST)
	public String taisen(String taisenyoyaku, HttpSession session, String reservationId) throws IOException {
		String year_class = (String) session.getAttribute("year_class");
		Dome_versus myVersus = new Dome_versus();
		myVersus.setReservation_id(Integer.parseInt(reservationId));
		myVersus.setVersus_class(year_class);
		dome_versusRepository.save(myVersus);

		return "redirect:/yoyaku_done";
	}

	//スポーツ大会機能
	@RequestMapping(path = "/taikai", method = RequestMethod.GET)
	public String taikai(Model model, HttpSession session) {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		// SELECT文の実行（del_flgが0の場合のみ）
		resultList = jdbcTemplate.queryForList("SELECT * FROM dome_sports WHERE del_flg = 0");

		// 実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "taikai";
	}

	@RequestMapping(path = "/taikai", method = RequestMethod.POST)
	public String taikai(Model model) throws IOException {
		// SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		// SELECT文の実行（del_flgが0の場合のみ）
		resultList = jdbcTemplate.queryForList("SELECT * FROM dome_sports WHERE del_flg = 0");

		// 実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("dome_sports", resultList);
		return "taikai";
	}

	//管理者ログイン画面
	@RequestMapping(path = "/administrator", method = RequestMethod.GET)
	public String administrator() throws IOException {
		return "administrator";
	}

	@RequestMapping(path = "/administrator", method = RequestMethod.POST)
	public String administrator(String user_id, String password, RedirectAttributes redirectAttributes,
			HttpSession session, String slogin) throws IOException {
		if (slogin != null) {
			return "redirect:/slogin";
		}

		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList("SELECT * FROM dome_administrator WHERE user_id = ? and password = ?", user_id, password);

		// 先にIDの検証
		if (!CollectionUtils.isEmpty(resultList)) {
			String user_name = (String) resultList.get(0).get("user_name");

			// セッションに必要な情報を保存
			session.setAttribute("user_id", user_id);
			session.setAttribute("user_name", user_name);
			session.setAttribute("password", password);

			return "redirect:/management";
		}
		return "redirect:/administrator";
	}

	@RequestMapping(path = "/management", method = RequestMethod.GET)
	public String management(Model model, HttpSession session) throws IOException {
		// セッションからユーザー情報を取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		// ユーザー情報が不足している場合にログイン画面にリダイレクトするロジックを追加することも検討してください

		return "management";
	}

	@RequestMapping(path = "/management", method = RequestMethod.POST)
	public String management(String studentregistration, String administratorregistration, String reservationmanagement,
			String sportsmanagement)
			throws IOException {
		if (studentregistration != null) {
			return "redirect:/studentregistration";
		} else if (administratorregistration != null) {
			return "redirect:/administratorregistration";
		} else if (reservationmanagement != null) {
			return "redirect:/reservationmanagement";
		} else if (sportsmanagement != null) {
			return "redirect:/sportsmanagement";
		}

		return "management";

	}

	//管理者メインメニューへ
	@RequestMapping(path = "/adminmain", method = RequestMethod.POST)
	public String adminmain(Model model, String user_name, HttpSession session) throws IOException {

		return "redirect:/management";
	}

	//ユーザー管理
	@RequestMapping(path = "/studentregistration", method = RequestMethod.GET)
	public String studentregistration(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "studentregistration";
	}

	@RequestMapping(path = "/studentregistration", method = RequestMethod.POST)
	public String studentregistration(@RequestParam int user_id,
			@RequestParam String year_class,
			@RequestParam String user_name,
			Model model) throws IOException {

		// user_idが既存のユーザーに対応するものであれば更新、存在しなければ新規登録
		int updateCount = jdbcTemplate.update(
				"UPDATE dome_user SET year_class = ?, user_name = ? WHERE user_id = ?",
				year_class, user_name, user_id);

		if (updateCount == 0) {
			// UPDATEが行われなかった場合（該当のuser_idが存在しない場合）は新規登録
			jdbcTemplate.update("INSERT INTO dome_user (user_id, year_class, user_name, del_flg) VALUES (?, ?, ?, ?)",
					user_id, year_class, user_name, 0); // 0は初期のdel_flgの値として仮定
		}

		return "redirect:/studentregistration";
	}

	//管理者管理
	@RequestMapping(path = "/administratorregistration", method = RequestMethod.GET)
	public String administratorregistration(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "administratorregistration";
	}

	@RequestMapping(path = "/administratorregistration", method = RequestMethod.POST)
	public String administratorregistration(@RequestParam String user_name,
			@RequestParam String password,
			Model model) throws IOException {

		// ユーザー名が既に存在するか確認
		boolean isUserExists = checkIfUserExists(user_name);

		if (isUserExists) {
			// 既に存在する場合はエラーメッセージなどを設定し、適切な処理を行う
			model.addAttribute("error", "その名前は既に登録されています。");
			return "administratorregistration";
		} else {
			// 新規登録
			jdbcTemplate.update("INSERT INTO dome_administrator (user_name, password, del_flg) VALUES (?, ?, ?)",
					user_name, password, 0);

			return "administratorregistration";
		}
	}

	private boolean checkIfUserExists(String user_name) {
		// データベースでユーザー名の重複を確認するクエリを実行
		int count = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM dome_administrator WHERE user_name = ?",
				Integer.class, user_name);

		return count > 0;
	}

	//予約管理
	@RequestMapping(path = "/reservationmanagement", method = RequestMethod.GET)
	public String showReservations(Model model, HttpSession session) {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		// 予約情報を取得
		List<Map<String, Object>> reservations = jdbcTemplate.queryForList(
				"SELECT * FROM dome_reservation");

		model.addAttribute("selectResult", reservations);

		return "reservationmanagement";
	}

	@RequestMapping(path = "/reservationmanagement", method = RequestMethod.POST)
	public String handleReservationAction(Model model, @RequestParam String action, @RequestParam int reservationId) {
		if ("deleteReservation".equals(action)) {
			// 予約の削除処理
			jdbcTemplate.update("DELETE FROM dome_reservation WHERE reservation_id = ?", reservationId);
			jdbcTemplate.update("DELETE FROM dome_calendar WHERE calendar_id = ?", reservationId);
		}

		// 再度予約情報を取得して表示
		List<Map<String, Object>> reservations = jdbcTemplate.queryForList(
				"SELECT * FROM dome_reservation");

		model.addAttribute("selectResult", reservations);

		return "redirect:/reservationmanagement";
	}

	//スポーツ大会管理
	@RequestMapping(path = "/sportsmanagement", method = RequestMethod.GET)
	public String sportsmanagement(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("SELECT * FROM dome_sports WHERE del_flg = 0");

		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "sportsmanagement";
	}

	@RequestMapping(path = "/sportsmanagementupdate", method = RequestMethod.POST)
	public String sportsmanagementupdate(Model model,
			@RequestParam String action,
			@RequestParam(name = "tournamentId", required = false) Long tournamentId) {

		if ("deleteTournament".equals(action) && tournamentId != null) {
			// 削除の処理
			jdbcTemplate.update("UPDATE dome_sports SET del_flg = 1 WHERE tournament_id = ?", tournamentId);
		}

		// 再度大会情報を取得して表示
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList("SELECT * FROM dome_sports WHERE del_flg = 0");
		model.addAttribute("selectResult", resultList);

		return "redirect:/sportsmanagement";
	}

	@RequestMapping(path = "/sportsmanagementinsert", method = RequestMethod.POST)
	public String sportsmanagementinsert(@RequestParam("tournament_image") MultipartFile tournament_image,
			@RequestParam("tournament_name") String tournament_name,
			Model model) throws IOException {

		byte[] byteData = tournament_image.getBytes();
		String encodedImage = Base64.getEncoder().encodeToString(byteData);

		// 現在の日時を取得
		Instant currentInstant = Instant.now();
		Timestamp tournament_data = Timestamp.from(currentInstant);

		// データベースに新しい大会データを挿入
		jdbcTemplate.update(
				"INSERT INTO dome_sports (tournament_image, tournament_name, tournament_data) VALUES (?, ?, ?)",
				encodedImage, tournament_name, tournament_data);

		return "redirect:/sportsmanagement";
	}
}
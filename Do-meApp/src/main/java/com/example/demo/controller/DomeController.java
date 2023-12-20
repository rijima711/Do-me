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

import com.example.demo.entity.Dome_reservation;
import com.example.demo.entity.Dome_versus;
import com.example.demo.repository.Dome_reservationRepository;
import com.example.demo.repository.Dome_versusRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class DomeController {
	@Autowired
	Dome_reservationRepository dome_reservationRepository;
	//Dome_userRepository dome_userRepository;
	@Autowired
	Dome_versusRepository dome_versusRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;

	//ログイン機能
	@RequestMapping(path = "/slogin", method = RequestMethod.GET)
	public String slogin() throws IOException {
		return "slogin";
	}

	@RequestMapping(path = "/slogin", method = RequestMethod.POST)
	public String slogin(String user_id, RedirectAttributes redirectAttributes, HttpSession session,
			String administrator)
			throws IOException {
		//管理者画面への遷移
		if (administrator != null) {
			return "redirect:/administrator";
		}
		List<Map<String, Object>> resultList = null;
		resultList = jdbcTemplate.queryForList("SELECT * FROM dome_user WHERE user_id = ?", user_id);

		if (!CollectionUtils.isEmpty(resultList)) {
			String user_name = (String) resultList.get(0).get("user_name");
			redirectAttributes.addAttribute("user_name", user_name);

			// セッションにユーザーIDを保存
			session.setAttribute("user_id", user_id);
			session.setAttribute("user_name", user_name);

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

	@RequestMapping(path = "/mainmenu", method = RequestMethod.GET)
	public String mainmenu(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
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
	public String yoyaku(String court, String date, String versus, RedirectAttributes redirectAttributes,
			HttpSession session)
			throws IOException {
		String user_id = (String) session.getAttribute("user_id");
		//		String user_id = (String) redirectAttributes.getFlashAttributes().get("user_id");
		Dome_reservation myReservation = new Dome_reservation();
		try {
			if (user_id != null) {
				if (versus != null) {
					myReservation.setDate(date);
					myReservation.setCourt(court);
					myReservation.setVersus(Integer.parseInt(versus));
					myReservation.setUser_id(Integer.parseInt(user_id));
					dome_reservationRepository.save(myReservation);
					return "redirect:/yoyaku_done";
				} else {
					myReservation.setDate(date);
					myReservation.setCourt(court);
					myReservation.setUser_id(Integer.parseInt(user_id));
					dome_reservationRepository.save(myReservation);
					return "redirect:/yoyaku_done";
				}
			} else {
				return "redirect:/yoyaku";
			}
		} catch (NumberFormatException e) {
			// エラーが発生した場合の処理
			e.printStackTrace(); // エラーをコンソールに表示
			// 例: エラーページにリダイレクト
			return "redirect:/error";
		}
	}

	//予約完了画面
	@RequestMapping(path = "/yoyaku_done", method = RequestMethod.GET)
	public String kanryou(Model model, HttpSession session) throws IOException {
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "yoyaku_done";
	}

	@RequestMapping(path = "/yoyaku_done", method = RequestMethod.POST)
	public String kanryou(RedirectAttributes redirectAttributes, String kanryou) throws IOException {
		// リダイレクト先での user_name 表示が必要ない場合は何もしなくても良い
		return "redirect:/mainmenu";
	}

	//予約状況確認画面
	@RequestMapping(path = "/joukyou", method = RequestMethod.GET)
	public String joukyou(Model model, String user_name, HttpSession session) throws IOException {
		List<Dome_reservation> reservationlist = dome_reservationRepository.findAll();
		model.addAttribute("reservationlist", reservationlist);
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
		List<Dome_reservation> reservationlist = dome_reservationRepository.findByVersus(1);

		model.addAttribute("reservationlist", reservationlist);
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "taisen";
	}

	@RequestMapping(path = "/taisen", method = RequestMethod.POST)
	public String taisen(String taisenyoyaku) throws IOException {

		Dome_versus myVersus = new Dome_versus();

		//Dome_versusRepository.save(myVersus);

		return "taisen";
	}

	//スポーツ大会画面
	@RequestMapping(path = "/taikai", method = RequestMethod.GET)
	public String taikai(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

		//SELECT文の結果をしまうためのリスト
		List<Map<String, Object>> resultList;

		//SELECT文の実行
		resultList = jdbcTemplate.queryForList("select * from dome_sports");

		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("dome_sports", resultList);

		return "taikai";
	}

	@RequestMapping(path = "/taikai", method = RequestMethod.POST)
	public String taikai(Model model) throws IOException {
		

		return "redirect:/taikai";
	}

	//管理者ログイン画面
	@RequestMapping(path = "/administrator", method = RequestMethod.GET)
	public String administrator() throws IOException {
		return "administrator";
	}

	@RequestMapping(path = "/administrator", method = RequestMethod.POST)
	public String administrator(String user_id, String password, RedirectAttributes redirectAttributes,
			HttpSession session) throws IOException {
		List<Map<String, Object>> resultList = jdbcTemplate
				.queryForList("SELECT * FROM dome_administrator WHERE user_id = ? and password = ?", user_id, password);
		// 先にIDの検証
		if (!CollectionUtils.isEmpty(resultList)) {
			System.out.println("aaaaaaaaaaaa");
			String user_name = (String) resultList.get(0).get("user_name");
			redirectAttributes.addAttribute("user_name", user_name);

			// セッションにuser_id,user_nameを保存
			session.setAttribute("user_id", user_id);
			session.setAttribute("user_name", user_name);
			session.setAttribute("password", password);
			return "redirect:/management";
		}
		return "redirect:/administrator";
	}

	//管理者ホーム
	@RequestMapping(path = "/management", method = RequestMethod.GET)
	public String management(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);

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

	//ユーザー管理
	@RequestMapping(path = "/studentregistration", method = RequestMethod.GET)
	public String studentregistration(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "studentregistration";
	}

	@RequestMapping(path = "/studentregistration", method = RequestMethod.POST)
	public String studentregistration(Model model) throws IOException {

		return "administratorregistration";
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
	public String administratorregistration(Model model) throws IOException {

		return "administratorregistration";
	}

	//予約管理
	@RequestMapping(path = "/reservationmanagement", method = RequestMethod.GET)
	public String reservationmanagement(Model model, String user_name, HttpSession session) throws IOException {
		// セッションからユーザーIDを取得
		String username = (String) session.getAttribute("user_name");
		model.addAttribute("user_name", username);
		return "reservationmanagement";
	}

	@RequestMapping(path = "/reservationmanagement", method = RequestMethod.POST)
	public String reservationmanagement(Model model) throws IOException {

		return "reservationmanagement";
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
		resultList = jdbcTemplate.queryForList("select * from dome_sports");

		//実行結果をmodelにしまってHTMLで出せるようにする。
		model.addAttribute("selectResult", resultList);

		return "sportsmanagement";
	}

	@RequestMapping(path = "/sportsmanagement", method = RequestMethod.POST)
	public String sportsmanagement(@RequestParam("tournament_image") MultipartFile tournament_image,
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

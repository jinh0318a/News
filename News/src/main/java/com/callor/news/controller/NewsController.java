package com.callor.news.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.callor.news.dao.MainNewsDao;
import com.callor.news.dao.MediaDao;
import com.callor.news.dao.NewsDao;
import com.callor.news.models.MainNewsVO;
import com.callor.news.models.MediaVO;
import com.callor.news.models.NewsVO;
import com.callor.news.service.NewsService;

@Controller
@RequestMapping("/news")
public class NewsController {

	private final NewsService newsService;
	private final NewsDao newsDao;
	private final MediaDao mediaDao;
	private final MainNewsDao mainNewsDao;

	public NewsController(NewsService newsService, NewsDao newsDao, MediaDao mediaDao, MainNewsDao mainNewsDao) {
		super();
		this.newsService = newsService;
		this.newsDao = newsDao;
		this.mediaDao = mediaDao;
		this.mainNewsDao = mainNewsDao;
	}

	@RequestMapping(value = "/getNews", method = RequestMethod.GET)
	public String getNews() {
		List<NewsVO> newNewsList = newsService.getNews();
		newsService.saveNews(newNewsList);
		return "redirect:/";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(@RequestParam(value = "country", defaultValue = "kr") String country, Model model) {
		try {
			// 뉴스 목록 가져오기
			List<NewsVO> newsList = newsService.getTopHeadlinesByCountry(country);

			// 모델에 뉴스 리스트 추가
			model.addAttribute("newsList", newsList);
			return "news/list";

			// return "news/list :: headlineContent";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "뉴스 목록을 가져오는 데 실패했습니다.");
			return "news/error"; // 오류 페이지로 리다이렉트
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam("word") String word, Model model) {
		try {
			// 뉴스 검색을 위한 서비스 메서드 호출
			List<NewsVO> resultList = newsService.searchNews(word);
			model.addAttribute("word", word);
			model.addAttribute("newsList", resultList);
			return "news/search"; // 검색 결과 페이지로 이동

		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "자격 증명 로드 실패");
			return "news/error"; // 오류 페이지로 이동
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "뉴스 검색 실패");
			return "news/error"; // 오류 페이지로 이동
		}
	}

	@RequestMapping(value = "/article/{n_no}", method = RequestMethod.GET)
	public String getArticle(@PathVariable("n_no") String n_no, Model model) {
		NewsVO article = newsDao.findByNO(n_no);
		if (article != null) {
			model.addAttribute("article", article);
			return "news/article";
		} else {
			return "news/error";
		}
	}

	@RequestMapping(value = "/detail/{m_no}", method = RequestMethod.GET)
	public String getdetail(@PathVariable("m_no") String m_no, Model model) {
		MainNewsVO detail = mainNewsDao.findByNo(m_no);
		if (detail != null) {
			model.addAttribute("detail", detail);
			return "news/detail";
		} else {
			return "news/error";
		}
	}

	@RequestMapping(value = "/media", method = RequestMethod.GET)
	public String media(Model model) {
		List<MediaVO> mediaList = mediaDao.findByAll();

		model.addAttribute("mediaList", mediaList);

		return "news/media";
		// return "news/media :: mediaContent";
	}

	
}

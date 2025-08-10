package com.abood.hentiyomi

import eu.kanade.tachiyomi.source.AnimeHttpSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode
import eu.kanade.tachiyomi.network.GET
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ArabShentai : AnimeHttpSource() {

    override val name = "ArabShentai"
    override val baseUrl = "https://arabshentai.com"
    override val lang = "ar"
    override val supportsLatest = true

    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/latest?page=$page")

    override fun latestUpdatesParse(response: Response) = response.asJsoup()
        .select("div.latest-anime div.anime-card")
        .map { element ->
            SAnime.create().apply {
                title = element.selectFirst("h3")?.text() ?: ""
                setUrlWithoutDomain(element.selectFirst("a")?.attr("href") ?: "")
                thumbnail_url = element.selectFirst("img")?.attr("src") ?: ""
            }
        }

    override fun popularAnimeRequest(page: Int) = GET("$baseUrl/popular?page=$page")

    override fun popularAnimeParse(response: Response) = response.asJsoup()
        .select("div.popular-anime div.anime-card")
        .map { element ->
            SAnime.create().apply {
                title = element.selectFirst("h3")?.text() ?: ""
                setUrlWithoutDomain(element.selectFirst("a")?.attr("href") ?: "")
                thumbnail_url = element.selectFirst("img")?.attr("src") ?: ""
            }
        }

    override fun searchAnimeRequest(page: Int, query: String) = GET("$baseUrl/search?q=$query&page=$page")

    override fun searchAnimeParse(response: Response) = response.asJsoup()
        .select("div.search-results div.anime-card")
        .map { element ->
            SAnime.create().apply {
                title = element.selectFirst("h3")?.text() ?: ""
                setUrlWithoutDomain(element.selectFirst("a")?.attr("href") ?: "")
                thumbnail_url = element.selectFirst("img")?.attr("src") ?: ""
            }
        }

    override fun episodeListRequest(anime: SAnime) = GET(baseUrl + anime.url)

    override fun episodeListParse(response: Response) = response.asJsoup()
        .select("div.episode-list li.episode-item")
        .map { element ->
            SEpisode.create().apply {
                name = element.selectFirst("a")?.text() ?: ""
                setUrlWithoutDomain(element.selectFirst("a")?.attr("href") ?: "")
            }
        }

    override fun videoListRequest(episode: SEpisode) = GET(baseUrl + episode.url)

    override fun videoListParse(response: Response) = response.asJsoup()
        .select("video source")
        .map { element ->
            // You might need to adjust this selector based on site structure
            val videoUrl = element.attr("src")
            // Return a list of videos - simplified for example
            listOf(com.abood.hentiyomi.Video(videoUrl))
        }

    private fun Response.asJsoup(): Document = Jsoup.parse(body?.string() ?: "")
}

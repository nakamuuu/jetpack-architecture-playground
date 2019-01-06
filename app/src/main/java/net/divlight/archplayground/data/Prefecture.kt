package net.divlight.archplayground.data

import android.content.Context
import net.divlight.archplayground.R

enum class Prefecture(val rawValue: Int) {
    Hokkaido(1), Aomori(2), Iwate(3), Miyagi(4), Akita(5), Yamagata(6),
    Fukushima(7), Ibaraki(8), Tochigi(9), Gunma(10), Saitama(11), Chiba(12),
    Tokyo(13), Kanagawa(14), Niigata(15), Toyama(16), Ishikawa(17), Fukui(18),
    Yamanashi(19), Nagano(20), Gifu(21), Shizuoka(22), Aichi(23), Mie(24),
    Shiga(25), Kyoto(26), Osaka(27), Hyogo(28), Nara(29), Wakayama(30),
    Tottori(31), Shimane(32), Okayama(33), Hiroshima(34), Yamaguchi(35), Tokushima(36),
    Kagawa(37), Ehime(38), Kochi(39), Fukuoka(40), Saga(41), Nagasaki(42),
    Kumamoto(43), Oita(44), Miyazaki(45), Kagoshima(46), Okinawa(47);

    fun getDisplayString(context: Context): String = context.resources
        .getStringArray(R.array.prefectures)[rawValue]
}

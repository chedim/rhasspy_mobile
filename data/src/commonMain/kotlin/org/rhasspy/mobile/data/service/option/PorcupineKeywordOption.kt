package org.rhasspy.mobile.data.service.option

import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.resources.MR

@Serializable
enum class PorcupineKeywordOption(
    override val text: StableStringResource,
    val file: FileResource,
    val language: PorcupineLanguageOption
) : IOption<PorcupineKeywordOption> {

    //en
    ALEXA(
        MR.strings.alexa.stable,
        MR.files.alexa_android,
        PorcupineLanguageOption.EN
    ),
    AMERICANO(
        MR.strings.americano.stable,
        MR.files.americano_android,
        PorcupineLanguageOption.EN
    ),
    BLUEBERRY(
        MR.strings.blueberry.stable,
        MR.files.blueberry_android,
        PorcupineLanguageOption.EN
    ),
    BUMBLEBEE(
        MR.strings.bumblebee.stable,
        MR.files.bumblebee_android,
        PorcupineLanguageOption.EN
    ),
    COMPUTER(
        MR.strings.computer.stable,
        MR.files.computer_android,
        PorcupineLanguageOption.EN
    ),
    GRAPEFRUIT(
        MR.strings.grapefruit.stable,
        MR.files.grapefruit_android,
        PorcupineLanguageOption.EN
    ),
    GRASSHOPPER(
        MR.strings.grasshopper.stable,
        MR.files.grasshopper_android,
        PorcupineLanguageOption.EN
    ),
    HEY_BARISTA(
        MR.strings.hey_barista.stable,
        MR.files.hey20barista_android,
        PorcupineLanguageOption.EN
    ),
    HEY_GOOGLE(
        MR.strings.hey_google.stable,
        MR.files.hey20google_android,
        PorcupineLanguageOption.EN
    ),
    HEY_SIRI(
        MR.strings.hey_siri.stable,
        MR.files.hey20siri_android,
        PorcupineLanguageOption.EN
    ),
    JARVIS(
        MR.strings.jarvis.stable,
        MR.files.jarvis_android,
        PorcupineLanguageOption.EN
    ),
    OK_GOOGLE(
        MR.strings.ok_google.stable,
        MR.files.ok20google_android,
        PorcupineLanguageOption.EN
    ),
    PICO_CLOCK(
        MR.strings.pico_clock.stable,
        MR.files.pico20clock_android,
        PorcupineLanguageOption.EN
    ),
    PICOVOICE(
        MR.strings.picovoice.stable,
        MR.files.picovoice_android,
        PorcupineLanguageOption.EN
    ),
    PORCUPINE(
        MR.strings.porcupine.stable,
        MR.files.porcupine_android,
        PorcupineLanguageOption.EN
    ),
    TERMINATOR(
        MR.strings.terminator.stable,
        MR.files.terminator_android,
        PorcupineLanguageOption.EN
    ),

    //ar
    OCTOPUS(
        MR.strings.octopus_ar.stable,
        MR.files.D8A3D8AED8B7D8A8D988D8B7_android,
        PorcupineLanguageOption.AR
    ),
    HUMUS(
        MR.strings.humus_ar.stable,
        MR.files.D8A7D984D8ADD985D8B5_android,
        PorcupineLanguageOption.AR
    ),
    COFFEE(
        MR.strings.coffee_ar.stable,
        MR.files.D982D987D988D8A9_android,
        PorcupineLanguageOption.AR
    ),
    TOASTER(
        MR.strings.toaster_ar.stable,
        MR.files.D985D8ADD985D8B5D8A9_android,
        PorcupineLanguageOption.AR
    ),

    //de
    ANANAS(
        MR.strings.ananas_de.stable,
        MR.files.ananas_android,
        PorcupineLanguageOption.DE
    ),
    HEUSCHRECKE(
        MR.strings.heuschrecke_de.stable,
        MR.files.heuschrecke_android,
        PorcupineLanguageOption.DE
    ),
    HIMBEERE(
        MR.strings.himbeere_de.stable,
        MR.files.himbeere_android,
        PorcupineLanguageOption.DE
    ),
    LEGUAN(
        MR.strings.leguan_de.stable,
        MR.files.leguan_android,
        PorcupineLanguageOption.DE
    ),
    STACHELSCHWEIN(
        MR.strings.stachelschwein_de.stable,
        MR.files.stachelschwein_android,
        PorcupineLanguageOption.DE
    ),

    //es
    EMPAREDADO(
        MR.strings.emparedado_es.stable,
        MR.files.emparedado_android,
        PorcupineLanguageOption.ES
    ),
    LEOPARDO(
        MR.strings.leopardo_es.stable,
        MR.files.leopardo_android,
        PorcupineLanguageOption.ES
    ),
    MANZANA(
        MR.strings.manzana_es.stable,
        MR.files.manzana_android,
        PorcupineLanguageOption.ES
    ),
    MURCIELAGO(
        MR.strings.murcielago_es.stable,
        MR.files.murciC3A9lago_android,
        PorcupineLanguageOption.ES
    ),

    //fa
    HEDGEHOG(
        MR.strings.hedgehog_fa.stable,
        MR.files.D8ACD988D8ACD98720D8AADB8CD8BADB8C_android,
        PorcupineLanguageOption.FA
    ),
    BYE(
        MR.strings.bye_fa.stable,
        MR.files.D8AED8AFD8A7D8ADD8A7D981D8B8_android,
        PorcupineLanguageOption.FA
    ),
    GOOD_MORNING(
        MR.strings.good_morning_fa.stable,
        MR.files.D8B5D8A8D8AD20D8A8D8AEDB8CD8B1_android,
        PorcupineLanguageOption.FA
    ),

    //fr
    FRAMBOISE(
        MR.strings.framboise_fr.stable,
        MR.files.framboise_android,
        PorcupineLanguageOption.FR
    ),
    MON_CHOUCHOU(
        MR.strings.mon_chouchou_fr.stable,
        MR.files.mon20chouchou_android,
        PorcupineLanguageOption.FR
    ),
    PARAPLUIE(
        MR.strings.parapluie_fr.stable,
        MR.files.parapluie_android,
        PorcupineLanguageOption.FR
    ),
    PERROQUET(
        MR.strings.perroquet_fr.stable,
        MR.files.perroquet_android,
        PorcupineLanguageOption.FR
    ),
    TOURNESOL(
        MR.strings.tournesol_fr.stable,
        MR.files.tournesol_android,
        PorcupineLanguageOption.FR
    ),

    //hi
    NAMASTE(
        MR.strings.namaste_hi.stable,
        MR.files.E0A4A8E0A4AEE0A4B8E0A58DE0A4A4E0A587_android,
        PorcupineLanguageOption.HI
    ),
    MOHABBAT(
        MR.strings.mohabbat_hi.stable,
        MR.files.E0A4AEE0A58BE0A4B9E0A4ACE0A58DE0A4ACE0A4A4_android,
        PorcupineLanguageOption.HI
    ),
    VIDAI(
        MR.strings.vidai_hi.stable,
        MR.files.E0A4B5E0A4BFE0A4A6E0A4BEE0A488_android,
        PorcupineLanguageOption.HI
    ),
    SUBHAGA(
        MR.strings.subhaga_hi.stable,
        MR.files.E0A4B8E0A581E0A4ADE0A497_android,
        PorcupineLanguageOption.HI
    ),

    //it
    CAMERIERE(
        MR.strings.cameriere_it.stable,
        MR.files.cameriere_android,
        PorcupineLanguageOption.IT
    ),
    ESPRESSO(
        MR.strings.espresso_it.stable,
        MR.files.espresso_android,
        PorcupineLanguageOption.IT
    ),
    PORCOSPINO(
        MR.strings.porcospino_it.stable,
        MR.files.porcospino_android,
        PorcupineLanguageOption.IT
    ),
    SILENZIO_BRUNO(
        MR.strings.silencio_bruno_it.stable,
        MR.files.silenzio20bruno_android,
        PorcupineLanguageOption.IT
    ),

    //ja
    RINGO(
        MR.strings.ringo_ja.stable,
        MR.files.E3828AE38293E38194_android,
        PorcupineLanguageOption.JA
    ),
    BUSHI(
        MR.strings.bushi_ja.stable,
        MR.files.E5BF8DE88085_android,
        PorcupineLanguageOption.JA
    ),
    NINJA(
        MR.strings.ninja_ja.stable,
        MR.files.E6ADA6E5A3AB_android,
        PorcupineLanguageOption.JA
    ),

    //ko
    AISEUKEULIM(
        MR.strings.aieseukeulim_ko.stable,
        MR.files.EC9584EC9DB4EC8AA4ED81ACEBA6BC_android,
        PorcupineLanguageOption.KO
    ),
    BIGSEUBI(
        MR.strings.bigseubi_ko.stable,
        MR.files.EBB985EC8AA4EBB984_android,
        PorcupineLanguageOption.KO
    ),
    KOPPULSO(
        MR.strings.koppulso_ko.stable,
        MR.files.ECBD94EBBF94EC868C_android,
        PorcupineLanguageOption.KO
    ),

    //nl
    BROODROOSTER(
        MR.strings.broodrooster_nl.stable,
        MR.files.broodrooster_android,
        PorcupineLanguageOption.NL
    ),
    HOI_LOTTE(
        MR.strings.hoiLotte_nl.stable,
        MR.files.hoi20lotte_android,
        PorcupineLanguageOption.NL
    ),
    KOFFIE(
        MR.strings.koffie_nl.stable,
        MR.files.koffie_android,
        PorcupineLanguageOption.NL
    ),
    STEKELVARKEN(
        MR.strings.stekelvarken_nl.stable,
        MR.files.stekelvarken_android,
        PorcupineLanguageOption.NL
    ),

    //pl
    JEZOZWIERZ(
        MR.strings.jezozwierz_pl.stable,
        MR.files.jeC5BCozwierz_android,
        PorcupineLanguageOption.PL
    ),
    KAWA(
        MR.strings.kawa_pl.stable,
        MR.files.kawa_android,
        PorcupineLanguageOption.PL
    ),
    PIEROGI(
        MR.strings.pierogi_pl.stable,
        MR.files.pierogi_android,
        PorcupineLanguageOption.PL
    ),
    ZUBROWKA(
        MR.strings.zubrowka_pl.stable,
        MR.files.C5BCubrC3B3wka_android,
        PorcupineLanguageOption.PL
    ),

    //pt
    ABACAXI(
        MR.strings.abacaxi_pt.stable,
        MR.files.abacaxi_android,
        PorcupineLanguageOption.PT
    ),
    FENOMENO(
        MR.strings.fenomeno_pt.stable,
        MR.files.fenC3B4meno_android,
        PorcupineLanguageOption.PT
    ),
    FORMIGA(
        MR.strings.formiga_pt.stable,
        MR.files.formiga_android,
        PorcupineLanguageOption.PT
    ),
    PORCO_ESPINHO(
        MR.strings.porcoEspinho_pt.stable,
        MR.files.porcoespinho_android,
        PorcupineLanguageOption.PT
    ),

    //ru
    VNIMANIYE(
        MR.strings.vnimaniye_ru.stable,
        MR.files.D0B2D0BDD0B8D0BCD0B0D0BDD0B8D0B5_android,
        PorcupineLanguageOption.RU
    ),
    OSTOROZHNYY(
        MR.strings.ostorozhnyy_ru.stable,
        MR.files.D0BED181D182D0BED180D0BED0B6D0BDD18BD0B9_android,
        PorcupineLanguageOption.RU
    ),
    OTKRYTO(
        MR.strings.otkryto_ru.stable,
        MR.files.D0BED182D0BAD180D18BD182D0BE_android,
        PorcupineLanguageOption.RU
    ),
    POMOGITE(
        MR.strings.pomogite_ru.stable,
        MR.files.D0BFD0BED0BCD0BED0B3D0B8D182D0B5_android,
        PorcupineLanguageOption.RU
    ),

    //sv
    HYGGE(
        MR.strings.hygge_sv.stable,
        MR.files.D0BFD0BED0BCD0BED0B3D0B8D182D0B5_android,
        PorcupineLanguageOption.SV
    ),
    KAFFEE(
        MR.strings.kaffee_sv.stable,
        MR.files.D0BFD0BED0BCD0BED0B3D0B8D182D0B5_android,
        PorcupineLanguageOption.SV
    ),
    KOETTBULLAR(
        MR.strings.koettbullar_sv.stable,
        MR.files.D0BFD0BED0BCD0BED0B3D0B8D182D0B5_android,
        PorcupineLanguageOption.SV
    ),
    PIGGSVIN(
        MR.strings.piggsvin_sv.stable,
        MR.files.D0BFD0BED0BCD0BED0B3D0B8D182D0B5_android,
        PorcupineLanguageOption.SV
    ),

    //vn
    CHAO_CHI(
        MR.strings.chao_chi_vn.stable,
        MR.files.chC3A0o20chE1BB8B_android,
        PorcupineLanguageOption.VN
    ),
    CON_NHIM(
        MR.strings.con_nhim_vn.stable,
        MR.files.con20nhC3ADm_android,
        PorcupineLanguageOption.VN
    ),
    MAY_NUONG_BAHN_MI(
        MR.strings.may_nuong_banh_mi_vn.stable,
        MR.files.mC3A1y20nC6B0E1BB9Bng20bC3A1nh20mC3AC_android,
        PorcupineLanguageOption.VN
    ),
    MAY_PHA_CA_PHE(
        MR.strings.may_pha_ca_phe_vn.stable,
        MR.files.mC3A1y20pha20cC3A020phC3AA_android,
        PorcupineLanguageOption.VN
    ),

    //zh
    HELLO_ZH(
        MR.strings.hello_zh.stable,
        MR.files.E4BDA0E5A5BD_android,
        PorcupineLanguageOption.ZH
    ),
    COFFEE_ZH(
        MR.strings.coffee_zh.stable,
        MR.files.E59296E595A1_android,
        PorcupineLanguageOption.ZH
    ),
    DUMPLINGS_ZH(
        MR.strings.dumplings_zh.stable,
        MR.files.E6B0B4E9A5BA_android,
        PorcupineLanguageOption.ZH
    ),
    PORCUPINE_ZH(
        MR.strings.porcupine_zh.stable,
        MR.files.E8B1AAE78CAA_android,
        PorcupineLanguageOption.ZH
    );

    override val internalEntries get() = entries

}
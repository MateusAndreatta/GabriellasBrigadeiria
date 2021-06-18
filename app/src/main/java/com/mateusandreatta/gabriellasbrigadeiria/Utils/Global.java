package com.mateusandreatta.gabriellasbrigadeiria.Utils;

import android.content.Context;

import com.mateusandreatta.gabriellasbrigadeiria.R;

import java.text.NumberFormat;
import java.util.Locale;

public class Global {

    public static String gabriellasBrigadeiriaAddress = "-25.512240092843843, -49.1847911890005";

    public static String translateFirebaseException(Context c, String msg){

        switch (msg) {
            case "The email address is badly formatted.":
                return c.getResources().getString(R.string.firebase_exception_email_badly_formattd);
            case "The given password is invalid. [ Password should be at least 6 characters ]":
                return c.getResources().getString(R.string.firebase_exception_password_invalid_least_6_characters);
            case "The email address is already in use by another account.":
                return c.getResources().getString(R.string.firebase_exception_email_already_in_use);
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                return c.getResources().getString(R.string.firebase_exception_no_user_record_with_identifier);
            case "The password is invalid or the user does not have a password.":
                return c.getResources().getString(R.string.firebase_exception_password_invalid);
            default:
                return msg;
        }
    }

    public static String formatCurrencyDoubleValue(Double price){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        return numberFormat.format(price);
    }

    public static Double getDoubleValueFromMaskedEditText(String s){
        String clean = s.replace(" ", "");
        clean = clean.replace(" ", "");
        clean = clean.replace("R$", "");
        clean = clean.replace(".","");
        clean = clean.replace(",", ".");
        return Double.parseDouble(clean);
    }

    public static final String[] NEIGHBORHOODS = new String[] {
            "Ganchinho", "Sitio Cercado", "Umbará", "Abranches", "Atuba", "Bacacheri", "Bairro Alto", "Barreirinha", "Boa Vista", "Cachoeira", "Pilarzinho", "Santa Cândida", "São Lourenço", "Taboão", "Tarumã", "Tingui", "Alto Boqueirão", "Boqueirão", "Hauer", "Xaxim", "Cajuru", "Capão da Imbuia", "Guabirotuba", "Jd. das Américas", "Uberaba", "Augusta", "Cidade Industrial", "Riviera", "São Miguel", "Água Verde", "Campo Comprido", "Fanny", "Fazendinha", "Guaíra", "Lindoia", "Novo Mundo", "Parolin", "Portão", "Santa Quitéria", "Vila Izabel", "Ahú", "Alto da Glória", "Alto da XV", "Batel", "Bigorrilho", "Bom Retiro", "Cabral", "Centro", "Centro Cívico", "Cristo Rei", "Hugo Lange", "Jardim Botânico", "Jardim Social", "Juvevê", "Mercês", "Prado Velho", "Rebouças", "São Francisco", "Campo de Santana", "Capão Raso", "Caximba", "Pinheirinho", "Tatuquara", "Butiatuvinha", "Campina do Siqueira", "Campo Comprido", "Cascatinha", "Lamenha Pequena", "Mossunguê", "Orleans", "Santa Felicidade", "Santo Inácio", "São Braz", "São João", "Seminário", "Vista Alegre", "Academia", "Afonso Pena", "Águas Belas", "Área Institucional Aeroportuária", "Aristocrata", "Arujá", "Aviação", "Barro Preto", "Bom Jesus", "Boneca do Iguaçu", "Borda do Campo", "Cachoeira", "Campina do Taquaral", "Campo Largo da Roseira", "Centro", "Cidade Jardim", "Colônia Rio Grande", "Contenda", "Costeira", "Cristal", "Cruzeiro", "Del Rey", "Dom Rodrigo", "Guatupê", "Iná", "Ipê", "Itália", "Jurema", "Miringuava", "Murici Urbano", "Ouro Fino", "Parque da Fonte", "Pedro Moro", "Quissisana", "Rio Pequeno", "Roseira de São Sebastião", "Santo Antônio", "São Cristovão", "São Domingos", "São Marcos", "São Pedro", "Zacarias"
    };

}

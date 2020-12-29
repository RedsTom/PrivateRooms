package org.reddev.privateroomsreborn.utils.general

import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest

class UnirestUtils {

    static String getCountryName(String countryCode) {
        HttpResponse<JsonNode> response = Unirest.get("https://restcountries.eu/rest/v2/alpha/${countryCode}")
                .asJson()
        return response.getBody().getObject().getJSONArray("languages").getJSONObject(0).getString("nativeName")
    }

    static String getCountryFlag(String countryCode) {
        HttpResponse<JsonNode> response = Unirest.get("https://restcountries.eu/rest/v2/alpha/${countryCode}")
                .asJson()
        return response.getBody().getObject().getString("flag")
    }

}

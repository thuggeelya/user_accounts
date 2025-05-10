package ru.thuggeelya.useraccounts.util;

public final class ElasticsearchQueryUtils {

    public static final String EMAIL_QUERY = """
            {
                "bool": {
                    "filter": [
                        {
                            "term": {
                                "email_data.keyword": "?0"
                            }
                        }
                    ]
                }
            }
            """;

    public static final String PHONE_QUERY = """
            {
                "bool": {
                    "filter": [
                        {
                            "term": {
                                "phone_data.keyword": "?0"
                            }
                        }
                    ]
                }
            }
            """;
}

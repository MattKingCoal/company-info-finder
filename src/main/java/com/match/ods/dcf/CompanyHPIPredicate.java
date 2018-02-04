package com.match.ods.dcf;

import java.util.function.Predicate;

public class CompanyHPIPredicate implements Predicate<Doc> {

    @Override
    public boolean test(Doc t) {
        if ("HPI".equals(t.getCompany()))
            return true;
        return false;
    }

}

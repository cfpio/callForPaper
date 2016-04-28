/*
 * Copyright (c) 2016 BreizhCamp
 * [http://breizhcamp.org]
 *
 * This file is part of CFP.io.
 *
 * CFP.io is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by tmaugin on 22/05/2015.
 */
import io.cfp.config.filter.CsrfFilter;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CsrfFilterTest {

    private MockFilterChain chain;
    private MockHttpServletRequest req;
    private MockHttpServletResponse rep;

    @Before
    public void setUp()
    {
        chain = new MockFilterChain();
        req = new MockHttpServletRequest();
        rep = new MockHttpServletResponse();
    }

    @Test
    public void test1_csrfFilterPass()
    {
        boolean hadError = false;
        CsrfFilter csrfFilter = new CsrfFilter();
        req.addHeader("X-CSRF-TOKEN","sf927dfsg84665fdg45df");
        req.setCookies(new Cookie("CSRF-TOKEN", "sf927dfsg84665fdg45df"));
        try {
            csrfFilter.doFilter(req, rep, chain);
        } catch (IOException e) {
            hadError = true;
            e.printStackTrace();
        } catch (ServletException e) {
            hadError = true;
            e.printStackTrace();
        }

        assertEquals(false, hadError);
        assertEquals(200, rep.getStatus());
    }

    @Test
    public void test2_csrfFilterMissMatch()
    {
        boolean hadError = false;
        CsrfFilter csrfFilter = new CsrfFilter();
        req.addHeader("X-CSRF-TOKEN","65fdg45df");
        req.setCookies(new Cookie("CSRF-TOKEN", "sf927dfsg84665fdg45df"));
        try {
            csrfFilter.doFilter(req, rep, chain);
        } catch (IOException e) {
            hadError = true;
            e.printStackTrace();
        } catch (ServletException e) {
            hadError = true;
            e.printStackTrace();
        }

        assertEquals(false, hadError);
        assertEquals(403, rep.getStatus());
    }

    @Test
    public void test3_csrfFilterMissMatch2()
    {
        boolean hadError = false;
        CsrfFilter csrfFilter = new CsrfFilter();
        req.addHeader("X-CSRF-TOKEN","sf927dfsg84665fdg45df");
        req.setCookies(new Cookie("CSRF-TOKEN", "84665fdg45df"));
        try {
            csrfFilter.doFilter(req, rep, chain);
        } catch (IOException e) {
            hadError = true;
            e.printStackTrace();
        } catch (ServletException e) {
            hadError = true;
            e.printStackTrace();
        }

        assertEquals(false, hadError);
        assertEquals(403, rep.getStatus());
    }

    @Test
    public void test4_csrfFilterNoHeader()
    {
        boolean hadError = false;
        CsrfFilter csrfFilter = new CsrfFilter();
        req.setCookies(new Cookie("CSRF-TOKEN", "sf927dfsg84665fdg45df"));
        try {
            csrfFilter.doFilter(req, rep, chain);
        } catch (IOException e) {
            hadError = true;
            e.printStackTrace();
        } catch (ServletException e) {
            hadError = true;
            e.printStackTrace();
        }

        assertEquals(false, hadError);
        assertEquals(403, rep.getStatus());
    }

    @Test
    public void test5_csrfFilterNoCookie()
    {
        boolean hadError = false;
        CsrfFilter csrfFilter = new CsrfFilter();
        req.addHeader("X-CSRF-TOKEN", "sf927dfsg84665fdg45df");
        try {
            csrfFilter.doFilter(req, rep, chain);
        } catch (IOException e) {
            hadError = true;
            e.printStackTrace();
        } catch (ServletException e) {
            hadError = true;
            e.printStackTrace();
        }

        assertEquals(false, hadError);
        assertEquals(403, rep.getStatus());
    }
}

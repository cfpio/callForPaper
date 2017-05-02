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

package io.cfp.multitenant;

import io.cfp.multitenant.TenantFilter;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class TenantFilterTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private TenantFilter filter = new TenantFilter();

    @Test
    public void should_retrieve_tenant_from_Origin() {
        request.setPathInfo("/some/api");
        request.addHeader(HttpHeaders.ORIGIN, "https://test.cfp.io" );
        assertEquals("test", filter.extractTenant(request));
    }

    @Test
    public void should_retrieve_tenant_from_Referer() {
        request.setPathInfo("/some/api");
        request.addHeader(HttpHeaders.REFERER, "https://test.cfp.io/" );
        assertEquals("test", filter.extractTenant(request));
    }

    @Test
    public void should_retrieve_tenant_from_XTenant_header() {
        request.setPathInfo("/some/api");
        request.addHeader(TenantFilter.TENANT_HEADER, "test" );
        assertEquals("test", filter.extractTenant(request));
    }

    @Test
    public void should_retrieve_tenant_from_Path() {
        request.setPathInfo("/events/test/something");
        assertEquals("test", filter.extractTenant(request));
    }

}

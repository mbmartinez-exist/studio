/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2017 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException
import org.apache.commons.lang3.StringUtils
import org.craftercms.studio.api.v1.exception.CmisRepositoryNotFoundException
import org.craftercms.studio.api.v1.exception.CmisTimeoutException
import org.craftercms.studio.api.v1.exception.CmisUnavailableException;
import scripts.api.CmisServices

def result = [:]

def site = params.site_id
def cmisRepo = params.cmis_repo_id
def searchTerm = params.search_term
def path = params.path
def start = 0
def number = 25

/** Validate Parameters */
def invalidParams = false;
def paramsList = []

// site_id
try {
    if (StringUtils.isEmpty(site)) {
        invalidParams = true
        paramsList.add("site_id")
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("site_id")
}

// cmis_repo_id
try {
    if (StringUtils.isEmpty(cmisRepo)) {
        invalidParams = true
        paramsList.add("cmis_repo_id")
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("cmis_repo_id")
}

// search_term
try {
    if (StringUtils.isEmpty(searchTerm)) {
        invalidParams = true
        paramsList.add("search_term")
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("search_term")
}

// start
try {
    if (StringUtils.isNotEmpty(params.start)) {
        start = params.start.toInteger()
        if (start < 0) {
            invalidParams = true
            paramsList.add("start")
        }
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("start")
}

// number
try {
    if (StringUtils.isNotEmpty(params.number)) {
        number = params.number.toInteger()
        if (number < 0) {
            invalidParams = true
            paramsList.add("number")
        }
    }
} catch (Exception exc) {
    invalidParams = true
    paramsList.add("number")
}

if (invalidParams) {
    response.setStatus(400)
    result.message = "Invalid parameter(s): " + paramsList
} else {
    def context = CmisServices.createContext(applicationContext, request)
    try {
        def total = CmisServices.searchTotal(context, site, cmisRepo, searchTerm, path);
        def items = CmisServices.search(context, site, cmisRepo, searchTerm, path, start, number);
        if (items != null) {
            result.items = items
            result.total = total
            response.setStatus(200)
        } else {
            response.setStatus(500)
            result.message = "Internal server error"
        }
    } catch (CmisUnavailableException e) {
        response.setStatus(503)
        result.message = "CMIS Unavailable: \n" + e
    } catch (CmisUnauthorizedException e) {
        response.setStatus(401)
        result.message = "CMIS Unauthorized: \n" + e
    } catch (CmisRepositoryNotFoundException e) {
        response.setStatus(404)
        result.message = "CMIS Repository " + cmisRepo + " not found"
    } catch (CmisTimeoutException e) {
        response.setStatus(408)
        result.message = "CMIS Timeout: \n" + e
    } catch (Exception e) {
        response.setStatus(500)
        result.message = "Internal server error: \n" + e
    }
}

return result
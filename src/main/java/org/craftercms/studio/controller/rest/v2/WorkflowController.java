/*
 * Copyright (C) 2007-2021 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.studio.controller.rest.v2;

import org.craftercms.studio.api.v1.exception.SiteNotFoundException;
import org.craftercms.studio.api.v1.service.site.SiteService;
import org.craftercms.studio.api.v2.service.workflow.WorkflowService;
import org.craftercms.studio.model.rest.PaginatedResultList;
import org.craftercms.studio.model.rest.ResponseBody;
import org.craftercms.studio.model.rest.Result;
import org.craftercms.studio.model.rest.content.SandboxItem;
import org.craftercms.studio.model.workflow.ItemStatesPostRequestBody;
import org.craftercms.studio.model.workflow.UpdateItemStatesByQueryRequestBody;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.craftercms.studio.controller.rest.v2.RequestConstants.REQUEST_PARAM_LIMIT;
import static org.craftercms.studio.controller.rest.v2.RequestConstants.REQUEST_PARAM_OFFSET;
import static org.craftercms.studio.controller.rest.v2.RequestConstants.REQUEST_PARAM_PATH;
import static org.craftercms.studio.controller.rest.v2.RequestConstants.REQUEST_PARAM_SITEID;
import static org.craftercms.studio.controller.rest.v2.RequestConstants.REQUEST_PARAM_STATES;
import static org.craftercms.studio.controller.rest.v2.RequestMappingConstants.API_2;
import static org.craftercms.studio.controller.rest.v2.RequestMappingConstants.ITEM_STATES;
import static org.craftercms.studio.controller.rest.v2.RequestMappingConstants.UPDATE_ITEM_STATES_BY_QUERY;
import static org.craftercms.studio.controller.rest.v2.RequestMappingConstants.WORKFLOW;
import static org.craftercms.studio.controller.rest.v2.ResultConstants.RESULT_KEY_ITEMS;
import static org.craftercms.studio.model.rest.ApiResponse.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(API_2 + WORKFLOW)
public class WorkflowController {

    private WorkflowService workflowService;
    private SiteService siteService;

    @GetMapping(value = ITEM_STATES, produces = APPLICATION_JSON_VALUE)
    public ResponseBody getItemStates(@RequestParam(name = REQUEST_PARAM_SITEID) String siteId,
                                      @RequestParam(name = REQUEST_PARAM_PATH, required = false) Optional<String> rpPath,
                                      @RequestParam(name = REQUEST_PARAM_STATES, required = false) Optional<Long> rpStates,
                                      @RequestParam(value = REQUEST_PARAM_OFFSET, required = false, defaultValue = "0")
                                                  int offset,
                                      @RequestParam(value = REQUEST_PARAM_LIMIT, required = false, defaultValue = "10")
                                                  int limit) throws SiteNotFoundException {
        if (!siteService.exists(siteId)) {
            throw new SiteNotFoundException(siteId);
        }

        String path = rpPath.isPresent() ? rpPath.get() : null;
        Long states = rpStates.isPresent() ? rpStates.get() : null;
        int total = workflowService.getItemStatesTotal(siteId, path, states);
        List<SandboxItem> items = new ArrayList<SandboxItem>();

        if (total > 0) {
            items = workflowService.getItemStates(siteId, path, states, offset, limit);
        }

        ResponseBody responseBody = new ResponseBody();
        PaginatedResultList<SandboxItem> result = new PaginatedResultList<>();
        result.setTotal(total);
        result.setOffset(offset);
        result.setLimit(CollectionUtils.isEmpty(items) ? 0 : items.size());
        result.setResponse(OK);
        responseBody.setResult(result);
        result.setEntities(RESULT_KEY_ITEMS, items);
        return responseBody;
    }

    @PostMapping(value = ITEM_STATES, produces = APPLICATION_JSON_VALUE)
    public ResponseBody updateItemStates(@RequestBody ItemStatesPostRequestBody requestBody)
            throws SiteNotFoundException {
        if (!siteService.exists(requestBody.getSiteId())) {
            throw new SiteNotFoundException(requestBody.getSiteId());
        }

        workflowService.updateItemStates(requestBody.getSiteId(), requestBody.getItems(),
                requestBody.isClearSystemProcessing(), requestBody.isClearUserLocked(), requestBody.getLive(),
                requestBody.getStaged());

        ResponseBody responseBody = new ResponseBody();
        Result result = new Result();
        result.setResponse(OK);
        responseBody.setResult(result);
        return responseBody;
    }

    @PostMapping(value = UPDATE_ITEM_STATES_BY_QUERY, produces = APPLICATION_JSON_VALUE)
    public ResponseBody updateItemStatesByQuery(@RequestBody UpdateItemStatesByQueryRequestBody requestBody)
            throws SiteNotFoundException {
        if (!siteService.exists(requestBody.getQuery().getSiteId())) {
            throw new SiteNotFoundException(requestBody.getQuery().getSiteId());
        }

        workflowService.updateItemStatesByQuery(requestBody.getQuery().getSiteId(), requestBody.getQuery().getPath(),
                requestBody.getQuery().getStates(), requestBody.getUpdate().isClearSystemProcessing(),
                requestBody.getUpdate().isClearUserLocked(), requestBody.getUpdate().isLive(),
                requestBody.getUpdate().isStaged());

        ResponseBody responseBody = new ResponseBody();
        Result result = new Result();
        result.setResponse(OK);
        responseBody.setResult(result);
        return responseBody;
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}

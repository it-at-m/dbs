import { getAccessToken, getAPIBaseURL } from "@/util/Constants.ts";
import type Checklist from "@/api/persservice/Checklist.ts";

export default class ChecklistService {
  getChecklists(): Promise<Response> {
    //todo replace with openapi generated client when backend is finished
    const url = getAPIBaseURL() + "/clients/api/p13n-backend/checklist";

    return fetch(url, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
  }

  getChecklist(id: string): Promise<Response> {
    //todo replace with openapi generated client when backend is finished
    const url = getAPIBaseURL() + "/clients/api/p13n-backend/checklist/" + id;

    return fetch(url, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
  }

  updateChecklist(newChecklist: Checklist): Promise<Response> {
    //todo replace with openapi generated client when backend is finished
    const url = getAPIBaseURL() + "/clients/api/p13n-backend/checklist/" + newChecklist.id;

    return fetch(url, {
      method: "PUT",
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newChecklist),
      credentials: "include",
    });
  }

  checkChecklistentry(
    checklistID: string,
    serviceID: string
  ): Promise<Response> {
    //todo replace with openapi generated client when backend is finished
    const url = `${getAPIBaseURL()}/clients/api/p13n-backend/checklist/${checklistID}/${serviceID}/check`;

    return fetch(url, {
      method: "POST",
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
  }

  uncheckChecklistentry(
    checklistID: string,
    serviceID: string
  ): Promise<Response> {
    //todo replace with openapi generated client when backend is finished
    const url = `${getAPIBaseURL()}/clients/api/p13n-backend/checklist/${checklistID}/${serviceID}/uncheck`;

    return fetch(url, {
      method: "POST",
      headers: {
        Authorization: "Bearer " + getAccessToken(),
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
  }
}

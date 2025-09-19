import {getAccessToken, getAPIBaseURL} from "@/util/Constants.ts";

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
    })
  }
}

import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {ConversationViewModel} from "../../../models/chat/conversation-view-model";
import {Observable} from "rxjs";
import {MessageViewModel} from "../../../models/chat/message-view-model";
import {ConversationProvisionRequest} from "../../../models/chat/conversation-provision-request";
import {SendMessageRequest} from "../../../models/chat/send-message-request";
import {UnmuteAndProvisionRequest} from "../../../models/chat/unmute-and-provision-request";

@Injectable({
  providedIn: 'root'
})
export class ChatApiService {

  private myConvsUrl = `${environment.apiBaseUrl}/chat/my_conversations`;
  private convMessagesUrl = `${environment.apiBaseUrl}/chat/conv_messages`;
  private convProvisionUrl = `${environment.apiBaseUrl}/chat/conv_provision`;
  private sendMsgUrl = `${environment.apiBaseUrl}/chat/send_message`;
  private archiveUrl = `${environment.apiBaseUrl}/chat/archive_conv`;
  private muteUrl = `${environment.apiBaseUrl}/chat/mute_conv`;
  private unmuteUrl = `${environment.apiBaseUrl}/chat/unmute_conv`;

  constructor(private httpClient: HttpClient) { }

  getMyConversations(pageIdx: number, pageSize: number): Observable<Page<ConversationViewModel>> {
    const requestBody = { pageIdx, pageSize };

    return this.httpClient.post<Page<ConversationViewModel>>(this.myConvsUrl, requestBody);
  }

  getConversationMessages(
    pageIdx: number,
    pageSize: number,
    conversationId: number
  ): Observable<Page<MessageViewModel>> {
    const requestBody = {
      pageIdx,
      pageSize,
      conversationId: conversationId
    };

    return this.httpClient.post<Page<MessageViewModel>>(this.convMessagesUrl, requestBody);
  }

  conversationProvision(convRequest: ConversationProvisionRequest): Observable<ConversationViewModel> {
    return this.httpClient.post<ConversationViewModel>(this.convProvisionUrl, convRequest);
  }

  sendMessage(newMessageRq: SendMessageRequest): Observable<MessageViewModel> {
    return this.httpClient.post<MessageViewModel>(this.sendMsgUrl, newMessageRq);
  }

  archiveConvAndReturnConvs(toArchiveId: number, pageIdx: number, pageSize: number): Observable<Page<ConversationViewModel>> {
    const pageRequest = { pageIdx, pageSize};

    return this.httpClient.patch<Page<ConversationViewModel>>(`${this.archiveUrl}/${toArchiveId}`, pageRequest);
  }

  muteConvAndReturnConvs(toMuteId: number, pageIdx: number, pageSize: number): Observable<Page<ConversationViewModel>> {
    const pageRequest = { pageIdx, pageSize };

    return this.httpClient.patch<Page<ConversationViewModel>>(`${this.muteUrl}/${toMuteId}`, pageRequest);
  }

  unMuteConvAndReturn(unmute: UnmuteAndProvisionRequest): Observable<ConversationViewModel> {
    return this.httpClient.patch<ConversationViewModel>(this.unmuteUrl, unmute);
  }
}

package com.sc_fleetfinder.fleets.events;

import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.entities.chat.Message;
import lombok.Getter;

public record NewMessageExternalNotifyEvent(Users recipient,
                                            Message message) {

}

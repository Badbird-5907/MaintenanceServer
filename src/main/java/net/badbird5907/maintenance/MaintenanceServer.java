package net.badbird5907.maintenance;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundDisconnectPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.ServerClosedEvent;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

import java.awt.*;

public class MaintenanceServer {
    public static void main(String[] args) {
        System.out.println("Starting server...");
        SessionService sessionService = new SessionService();
        Server server = new TcpServer("0.0.0.0", 25565, MinecraftProtocol::new);
        server.setGlobalFlag(MinecraftConstants.SESSION_SERVICE_KEY, sessionService);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, true);
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, (ServerInfoBuilder) session ->
                new ServerStatusInfo(
                        new VersionInfo("Maintenance", 0x40000001),
                        new PlayerInfo(-1, -1, new GameProfile[0]),
                        Component.text("")
                                .style(Style.style(TextColor.color(NamedTextColor.RED)))
                                .append(Component.text("This server is under maintenance.")),
                        null
                )
        );
        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, (ServerLoginHandler) session -> {
                    session.send(new ClientboundDisconnectPacket(Component.text("")
                            .style(Style.style(TextColor.color(NamedTextColor.RED)))
                            .append(Component.text("This server is under maintenance."))));
                }
        );
        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void serverClosed(ServerClosedEvent event) {
                System.out.println("Server closed.");
            }

            @Override
            public void sessionAdded(SessionAddedEvent event) {

            }

            @Override
            public void sessionRemoved(SessionRemovedEvent event) {
            }
        });
        System.out.println("Binding server...");
        server.bind();
        System.out.println("Listening on 0.0.0.0:25565");
    }
}

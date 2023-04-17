package de.manator.mypermissions.web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.config.ConfigFile;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.io.FileHandler;
import de.manator.mypermissions.players.PlayerHandler;

/**
 * @author ManatorDE
 *
 */
public class HttpServer {
	/**
	 * The port the webserver runs on
	 */
	private int port;
	
	/**
	 * The new line String
	 */
	private static final String newLine = "\r\n";

	/**
	 * The ServerSocket this webserver runs on
	 */
	private ServerSocket socket;

	/**
	 * Boolean whether the server is running or not
	 */
	private boolean running;

	/**
	 * The main thread of the server
	 */
	private Thread t;

	/**
	 * A reference to the MyPermissions GroupHandler
	 */
	private GroupHandler gh;
	
	/**
     * A reference to the MyPermissions PlayerHandler
     */
	private PlayerHandler ph;

	/**
	 * The file path of the webserver
	 */
	private String filePath;

	/**
	 * A reference to the mein class of this plugin
	 */
	private Main main;

	/**
	 * A file storing the logged in users ips
	 */
	private File loggedIn;

	private Thread[] connections;

	public HttpServer(int port, Main main) {
		this.port = port;
		this.gh = main.getGroupHandler();
		this.ph = main.getPlayerHandler();
		this.main = main;
		this.filePath = main.getDataFolder().getAbsolutePath() + "/web";
		try {
			this.socket = new ServerSocket(this.port);
			loggedIn = new File(main.getDataFolder().getAbsolutePath() + "/loggedin.yml");
			if (!loggedIn.exists()) {
				loggedIn.createNewFile();
			} else {
			    PrintWriter pw = new PrintWriter(loggedIn);
			    pw.println();
			    pw.close();
			}
		} catch (IOException e) {
			main.getLogger().info("Creating ServerSocket failed!");
			e.printStackTrace();
		}
	}

	public void runServer() {
		connections = new Thread[5];
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					main.getLogger().info("[Server] --- MyPermissions Webserver started at "
							+ socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort() + " ---");
					running = true;
					while (running) {

						for (int i = 0; i < connections.length; i++) {
							if (connections[i] == null || !connections[i].isAlive()) {
								connections[i] = new Thread(new Runnable() {
									@Override
									public void run() {
										acceptConnection();
									}
								});
								connections[i].start();
								break;
							}
						}

					}
				} catch (Throwable tr) {
					tr.printStackTrace();
				}
			}

		}, "HttpServer");
		t.start();
	}

	public void acceptConnection() {
		Socket connection = null;
		try {
			connection = socket.accept();
		} catch (IOException e1) {
		}
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			OutputStream out = new BufferedOutputStream(connection.getOutputStream());
			PrintStream pout = new PrintStream(out);
			
			String request = in.readLine();
			if (request == null)
				return;

			while (true) {
				String ignore = in.readLine();
				if (ignore == null || ignore.length() == 0)
					break;
			}
			if (request.startsWith("GETGROUPS")) {
				String response = "";
				
				LinkedList<String> groups = new LinkedList<>();
				
				for (Group g : gh.getGroups()) {
					groups.add(g.getName());
				}
				
				groups.sort(Comparator.naturalOrder());
				
				for(String g : groups) {
				    if (g != null) {
                        response += "<OPTION value= " + g + ">" + g+ "</OPTION>\n";
                    }
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETGPERMS")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				for (String s : gh.getPermissions(gh.getGroup(name))) {
					response += s + "\n";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("SETGPERMS")) {
				String response = "";

				LinkedList<String> perms = new LinkedList<>();

				while (in.ready()) {
					perms.add(in.readLine());
				}

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");
				gh.setPermissions(perms, gh.getGroup(name));

				gh.loadGroups();
				main.reloadPlayers();

				response = "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETGNEGS")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				for (String s : gh.getNegatedPermissions(gh.getGroup(name))) {
					response += s + "\n";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("SETGNEGS")) {
				String response = "";

				LinkedList<String> perms = new LinkedList<>();

				while (in.ready()) {
					perms.add(in.readLine());
				}

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");
				gh.setNegatedPermissions(perms, gh.getGroup(name));

				response = "success";

				gh.loadGroups();
				main.reloadPlayers();

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETCONFIG")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				response += gh.getRank(gh.getGroup(name)) + "\n";
				response += gh.getPrefix(gh.getGroup(name)) + "\n";
				response += gh.getSuffix(gh.getGroup(name)) + "\n";
				response += gh.isOP(gh.getGroup(name));

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("SETCONFIG")) {
				String response = "";

				// write rank, prefix, suffix, op

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");
				LinkedList<String> lines = new LinkedList<>();

				while (in.ready()) {
					String l = in.readLine();
					lines.add(l);
				}
				int rank = 0;
				String prefix = "", suffix = "";
				boolean op = false;
				if (lines.size() == 4) {
					for (int i = 0; i < 4; i++) {
						if (i == 0) {
							rank = Integer.parseInt(lines.get(i));
						} else if (i == 1) {
							prefix = lines.get(i);
							if (prefix.equals("null")) {
								prefix = null;
							}
						} else if (i == 2) {
							suffix = lines.get(i);
							if (suffix.equals("null")) {
								suffix = null;
							}
						} else if (i == 3) {
							op = Boolean.parseBoolean(lines.get(i));
						}
					}
					boolean a = gh.setRank(gh.getGroup(name), rank), b, c, d;
					if (prefix == null) {
						b = gh.deletePrefix(gh.getGroup(name));
					} else {
						b = gh.setPrefix(gh.getGroup(name), prefix);
					}
					if (suffix == null) {
						c = gh.deleteSuffix(gh.getGroup(name));
					} else {
						c = gh.setSuffix(gh.getGroup(name), suffix);
					}

					d = gh.setOp(gh.getGroup(name), op);

					gh.loadGroups();
					main.reloadPlayers();

					if (a && b && c && d) {
						response = "success";
					}
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("CREATE")) {
				String response = "";

				// get name from url

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				if (gh.addGroup(name))
					response += "success";

				gh.loadGroups();
				main.reloadPlayers();

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETGPLAYERS")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");
				
				LinkedList<String> players = getPlayers(gh.getGroup(name));
                players.sort(Comparator.naturalOrder());

				for (String s : players) {
					response += "<OPTION value = " + s + ">" + s + "</OPTION>\n";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETOPLAYERS")) {
				String response = "";

				// get name from url

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				LinkedList<String> players = ph.getPlayers();
				players.removeAll(getPlayers(gh.getGroup(name)));
				players.sort(Comparator.naturalOrder());
				
				for (String s : players) {
					response += "<OPTION value = " + s + ">" + s + "</OPTION>\n";
				}

				response += "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("ADDPLAYER")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				if (ph.addGroup(gh.getGroup(name), in.readLine()))
					response += "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("REMOVEPLAYER")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				if (ph.removeGroup(gh.getGroup(name), in.readLine()))
					response += "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("DELGROUP")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				if (gh.deleteGroup(gh.getGroup(name)))
					response += "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETPLAYERS")) {
				String response = "";
				
				LinkedList<String> players = ph.getPlayers();
				players.sort(Comparator.naturalOrder());
				
				for (String p : players) {
					if (p != null) {
						response += "<OPTION value= " + p + ">" + p + "</OPTION>\n";
					}
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETPPERMS")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replaceAll("/", "");
				
				for (String s : ph.getPermissions(name)) {
					response += s + "\n";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("SETPPERMS")) {
				String response = "";

				LinkedList<String> perms = new LinkedList<>();

				while (in.ready()) {
					String l = in.readLine();
					perms.add(l);
					main.getLogger().info("PERM: " + l);
				}

				String name = request.split("\\s+")[1];
				name = name.replaceAll("/", "");

				ph.setPermissions(perms, name);
				response = "success";

				gh.loadGroups();
				main.reloadPlayers();

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GETPNEGS")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replaceAll("/", "");

				for (String s : ph.getNegatedPermissions(name)) {
					response += s + "\n";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("SETPNEGS")) {
				String response = "";

				LinkedList<String> perms = new LinkedList<>();

				while (in.ready()) {
					perms.add(in.readLine());
				}

				String name = request.split("\\s+")[1];
				name = name.replaceAll("/", "");

				ph.setNegatedPermissions(perms, name);
				response = "success";
				
				gh.loadGroups();
				main.reloadPlayers();
				
				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("LOGIN")) {
				String response = "";

				LinkedList<String> log = FileHandler.getLines(loggedIn);

				String username = in.readLine(), password = in.readLine();

				if (password == null)
					password = "";
				
				ConfigFile f = main.getConfigFile();
				if (f.getPassword().equals(password) && f.getUsername().equals(username)) {
					if (!log.contains(connection.getInetAddress() + "")) {
						log.add(connection.getInetAddress() + "");
						FileHandler.writeLines(log, loggedIn);
					}
				}
				
				final String ip = connection.getInetAddress() + "";
				
				Timer t = new Timer("MPTimer");
				t.schedule(new TimerTask() {
                    
                    @Override
                    public void run() {
                        log.remove(ip);
                        FileHandler.writeLines(log, loggedIn);
                    }
                }, 600000);
				
				response = "success";

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("LOGOUT")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				LinkedList<String> log = FileHandler.getLines(loggedIn);

				if (log.contains(connection.getInetAddress() + "")) {
					log.remove(connection.getInetAddress() + "");
					FileHandler.writeLines(log, loggedIn);
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("LOGGEDIN")) {
				String response = "";

				String name = request.split("\\s+")[1];
				name = name.replace("/", "");

				LinkedList<String> log = FileHandler.getLines(loggedIn);

				if (log.contains(connection.getInetAddress() + "")) {
					response = "true";
				} else {
					response = "false";
				}

				pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: text/html" + newLine + "Date: " + new Date()
						+ newLine + "Content-length: " + response.length() + newLine + newLine + response);

			} else if (request.startsWith("GET")) {
				String response = "";

				String file = request.replace("GET /", "").replace(" HTTP/1.1", "").replace(" HTTP/1.0", "");

				try {

					if (file == "" || file == "/") {
						file = "index.html";
					}

					File f = new File(file);

					if (f.isDirectory()) {
						file += "index.html";
					}

					file = filePath + "/" + file;

					f = new File(file);

					if (!f.exists()) {
						f = new File(filePath + "/err/404.html");
					}

					String type = Files.probeContentType(f.toPath().getFileName());

					if (file.endsWith("js") || file.endsWith("css") || type.startsWith("text")) {
						BufferedReader br = new BufferedReader(new FileReader(f));
						while (br.ready()) {
							response += br.readLine() + newLine;
						}

						if (file.endsWith("js"))
							type = "text/js";

						if (file.endsWith("css"))
							type = "text/css";

						pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: " + type + newLine + "Date: "
								+ new Date() + newLine + "Content-length: " + response.length() + newLine + newLine
								+ response);

						br.close();
					} else {
						pout.print("HTTP/1.0 200 OK" + newLine + "Content-Type: " + type + newLine + "Date: "
								+ new Date());
						InputStream fs = new FileInputStream(f);
						int n = fs.available();
						byte buf[] = new byte[1024];
						pout.println("Content_Length:" + n);
						pout.println("");
						while ((n = fs.read(buf)) >= 0) {
							out.write(buf, 0, n);
						}
						fs.close();
					}
				} catch (FileNotFoundException e) {
					pout.println("HTTP/1.1 404 FILE NOT FOUND" + newLine + newLine);
				}
			} else {
				pout.print("HTTP/1.0 400 Bad Request" + newLine + newLine);
			}

			pout.close();
		} catch (Throwable tri) {
		}
	}

	private LinkedList<String> getPlayers(Group g) {
		LinkedList<String> players = new LinkedList<String>();
		for (String s : ph.getPlayers()) {
			if (ph.isInGroup(s, g)) {
				players.add(s);
			}
		}
		return players;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public void stopServer() {
		for (Thread th : connections) {
			if(th != null) {
			    th.interrupt();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
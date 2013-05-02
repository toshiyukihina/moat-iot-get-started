<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=utf-8">
		<title>
			SimpleExampleGAE
		</title>
		<link type="text/css" rel="stylesheet" href="/stylesheets/bootstrap/bootstrap.css">
		<link type="text/css" rel="stylesheet" href="/stylesheets/bootstrap/bootstrap-responsive.css">
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
		<script type="text/javascript" language="javascript" src="/javascripts/bootstrap/bootstrap.min.js"></script>
		<script type="text/javascript" language="javascript" src="/javascripts/gallery.js"></script>
		<link rel="icon" type="image/png" href="/moat_icon.png">
		<style type="text/css">
body { 
  text-shadow: -1px -1px 0 white, 1px -1px 0 white, -1px 1px 0 white, 1px 1px 0 white;
}
		</style>
	</head>
	<body>
		<%@ page import="java.util.List" %>
		<%@ page import="java.util.ArrayList" %>
		<%@ page import="java.util.Map" %>
		<%@ page import="com.yourinventit.moat.gae.example.models.RequestHistory" %>
		<%@ page import="com.yourinventit.moat.gae.example.models.ShakeEvent" %>
		<%@ page import="com.yourinventit.moat.gae.example.models.SysDevice" %>
		<%@ page import="com.yourinventit.moat.gae.example.models.SysDmjob" %>
		<%@ page import="com.yourinventit.moat.gae.example.models.ZigBeeDevice" %>
		<%
		final List<SysDevice> devices = (List<SysDevice>) request.getAttribute("devices");
		final Map<String, List<ShakeEvent>> shakeEvents = (Map<String, List<ShakeEvent>>) request.getAttribute("shake_events");
		final Map<String, List<ZigBeeDevice>> zbDevices = (Map<String, List<ZigBeeDevice>>) request.getAttribute("zb_devices");
		final List<SysDmjob> jobList = (List<SysDmjob>) request.getAttribute("job_list");
		final List<RequestHistory> jobHistories = (List<RequestHistory>) request.getAttribute("job_histories");
		%>
		<div id="wrap">
			<div class="container">
				<div class="btn-group" style="float:right;">
				  <button class="btn" id="btn-off">Background OFF</button>
				  <button class="btn" id="btn-bck"><i class="icon-backward"></i></button>
				  <button class="btn" id="btn-fwd"><i class="icon-forward"></i></button>
				</div>
				<h1>
					<img src="/moat_icon.png" alt="MOAT IoT icon" height="36" width="36" style="vertical-align: -12%;"> MOAT IoT Simple Example App
				</h1>
				<h2>
					Devices
				</h2><% if (devices == null || devices.isEmpty()) {%>
				<p>
					Install the gateway app and the example Android app into your Android phone. Then tap the <img src="/moat_icon.png" alt="MOAT IoT icon" height="20" width="20"> icon.
				</p><% } %>
				<% for (SysDevice d : devices) { %>
				<table class="table table-striped table-bordered table-condensed">
					<tr>
						<th>
							Name
						</th>
						<th>
							Device ID
						</th>
						<th>
							Commands
						</th>
					</tr>
					<tr>
						<td>
							<%= d.getName() %>
						</td>
						<td>
							<%= d.getDeviceId() %>
						</td>
						<td>
							<a href="/dashboard/vibrate?name=<%= d.getName() %>" class="btn btn-primary">Morse Code <i class='icon-play-circle icon-white'></i></a>
							<a href="/dashboard/delete_device?device_uid=<%= d.getUid() %>" class="btn">Delete <i class='icon-off'></i></a>
						</td>
					</tr>
				</table>
				<h3>
					Shake Event History for <%= d.getName() %>
				</h3><%
				// Building a comma separated string
				final StringBuilder uids = new StringBuilder();
				final List<ShakeEvent> list = shakeEvents.get(d.getName());
				for (ShakeEvent se : list) {
					uids.append(se.getUid()).append(",");
				}
				if (uids.length() > 0) {
					uids.delete(uids.length() - 1, uids.length());
				}
				%>
				<table class="table">
					<tr>
						<td>
							<a href="/dashboard/delete_all_shake_events?uids=<%= uids.toString() %>&device_uid=<%= d.getUid() %>" class="btn btn-primary">Clear <i class='icon-fire icon-white'></i></a>
						</td>
					</tr>
				</table>
				<table class="table table-striped table-bordered table-condensed">
					<tr>
						<th>
							Time
						</th>
						<th>
							X
						</th>
						<th>
							Y
						</th>
						<th>
							Z
						</th>
						<th>
							Acceleration
						</th>
					</tr>
					<% for (ShakeEvent se : list) { %>
					<tr>
						<td>
							<%= new java.util.Date(se.getTime()) %>
						</td>
						<td>
							<%= se.getX() %>
						</td>
						<td>
							<%= se.getY() %>
						</td>
						<td>
							<%= se.getZ() %>
						</td>
						<td>
							<%= se.getAcceleration() %>
						</td>
					</tr>
					<% } %>
				</table>
				<%
				final List<ZigBeeDevice> zbList = zbDevices.get(d.getName());
				if (zbList.isEmpty() == false) {
				%>
				<h3>
					ZigBee Devices for <%= d.getName() %>
				</h3>
				<table class="table table-striped table-bordered table-condensed">
					<tr>
						<th>
							Last Updated
						</th>
						<th>
							LCD Text
						</th>
						<th>
							Temperature (Celcius)
						</th>
						<th>
							Button Pressed?
						</th>
						<th>
							Commands
						</th>
					</tr>
					<% for (ZigBeeDevice z : zbList) { %>
					<tr>
						<td>
							<%= new java.util.Date(z.getLastUpdated()) %>
						</td>
						<td>
							<form id="<%= z.getUid() %>.show_text_on_lcd" action="/dashboard/show_text_on_lcd">
							<input type="text" name="lcd_text" value="<%= z.getLcdText() %>" maxlength="17" />
							<input type="hidden" name="uid" value="<%= z.getUid() %>" />
							<input type="hidden" name="name" value="<%= d.getName() %>" />
							</form>
						</td>
						<td>
							<%= z.getTemperature() %>
						</td>
						<td>
							<%= z.isClicked() %>
						</td>
						<td>
							<a href="javascript:void(0);" onclick="document.forms['<%= z.getUid() %>.show_text_on_lcd'].submit();" class="btn btn-primary">Send Text<i class='icon-play-circle icon-white'></i></a>
							<a href="/dashboard/inquire_temp?name=<%= d.getName() %>&uid=<%= z.getUid() %>" class="btn btn-primary">Inquire Temperature <i class='icon-play-circle icon-white'></i></a>
						</td>
					</tr>
					<%
					    }
					}
					%>
				</table>
				<hr>
				<% } // for (SysDevice d : devices) {} %>

				<h2>
					Active Requests
				</h2>
				<table class="table table-striped table-bordered table-condensed">
					<tr>
						<th>
							Device Name
						</th>
						<th>
							Status
						</th>
						<th>
							Type
						</th>
						<th>
							Started At
						</th>
						<th>
							Expired At
						</th>
						<th>
							Action
						</th>
					</tr>
					<% for (SysDmjob j : jobList) { %>
					<tr>
						<td>
							<%= j.getName() %>
						</td>
						<td>
							<%= j.getStatus() %>
						</td>
						<td>
							<%= j.getJobServiceId() %>
						</td>
						<td>
							<%= j.getStartedAt() %>
						</td>
						<td>
							<%= j.getExpiredAt() %>
						</td>
						<td>
							<a href="/dashboard/cancel?uid=<%= j.getUid() %>" class="btn btn-primary">Cancel <i class='icon-remove-sign icon-white'></i></a>
						</td>
					</tr>
					<% } %>
				</table>
				<h2>
					Request History
				</h2><% if (jobHistories != null && !jobHistories.isEmpty()) { %>
				<table class="table">
					<tr>
						<td>
							<a href="/request_histories/delete_all" class="btn btn-primary">Clear</a>
						</td>
					</tr>
				</table>
				<% } %>
				<table class="table table-striped table-bordered table-condensed">
					<tr>
						<th>
							Device Name
						</th>
						<th>
							Status
						</th>
						<th>
							Type
						</th>
						<th>
							Started At
						</th>
						<th>
							Ended At
						</th>
					</tr><% for (RequestHistory j : jobHistories) { %>
					<tr>
						<td>
							<%= j.getName() %>
						</td>
						<td>
							<%= j.getStatus() %>
						</td>
						<td>
							<%= j.getJobServiceId() %>
						</td>
						<td>
							<%= j.getStartedAt() %>
						</td>
						<td>
							<%= j.getEndedAt() %>
						</td>
					</tr>
					<% } %>
				</table>
			</div>
			<div id="footer">
				<div class="container">
					<p class="muted credit">
						Copyright &copy; 2013 <a href="http://www.yourinventit.com">Inventit Inc.</a>
					</p>
					<p class="muted credit">
						Built with <a href="http://dev.yourinventit.com/references/moat-rest-api-document">MOAT REST 1.0.1</a>, <a href="http://twitter.github.com/bootstrap/index.html">Bootstrap 2.2.2</a> and <a href="https://developers.google.com/appengine/">Google App Engine 1.7.6/1.7.7.1</a>.
					</p>
					<p class="muted credit">
						Running with <a href="http://dev.yourinventit.com">Inventit IoT Developer Network Development Sandbox Server</a>.
					</p>
					<p class="muted credit">
						Photos by <a href="http://www.flickr.com/photos/94782828@N05/">&copy;Yuko Homma</a> licensed under <a href="http://creativecommons.org/licenses/by-nd/2.1/jp/deed.en_US">CC BY-ND 2.1 JP</a>.
					</p>
				</div>
			</div>
		</div>
	</body>
</html>

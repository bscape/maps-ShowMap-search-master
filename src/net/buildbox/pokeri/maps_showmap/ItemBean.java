package net.buildbox.pokeri.maps_showmap;

public class ItemBean {
	private String name = "";
	private String url = "";
	private double range = 0;
	
//	public ItemBean(String tmp_name,String tmp_url){
//		name = tmp_name;
//		url = tmp_url;
//	}
	
	public void setName(String name) {
		this.name = name;
		}
	public String getName() {
		return name;
		}
	public void setUrl(String url) {
		this.url = url;
		}
	public String getUrl() {
		return url;
		}
	public double getRange() {
		return range;
	}
	public void setRange(double range) {
		this.range = range;
	}
	}
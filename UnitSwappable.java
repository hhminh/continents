public interface UnitSwappable{
	//the loaders are for further calculation
	public boolean transfer(UnitSwappable other, int i, UnitTypeLoader ul,
							HouseTypeLoader hl, TerrainLoader tl, OverlayLoader ol, TechLoader tel);

	public Unit get(int i);

	public int getCount();

	public void remove(int i);
}
package main

type get_app_data_ struct{}

var get_app_data = get_app_data_{}

// getting MPT
func (g *get_app_data_) GettingMpt(node *CoreNodeItem, mpr *TreeTag, tag_thesaurus map[string]int) {
	if node.IsTagged() {
		mpd := node.MostProminentDrill(tag_thesaurus)
		local_mpr := mpr
		for _, tag := range mpd {
			_, ok := local_mpr.Children[tag]
			if !ok {
				local_mpr.Children[tag] = newTreeTag(tag)
			}
			local_mpr = local_mpr.Children[tag]
		}
	}
}

// getting calendar
func (g *get_app_data_) GettingCalendar(node *CoreNodeItem, calendar *CoreDateFacet) {
	calendar.Month[node.Modified.Month] += 1
	calendar.Day[node.Modified.Day] += 1
	calendar.Year[node.Modified.Year] += 1
}

// possible tagging drilldowns
func (g *get_app_data_) PossibleTaggingDrillDowns(node *CoreNodeItem, cloud_can_select map[string]bool) {
	for _, tag := range node.Tags {
		cloud_can_select[tag] = true
	}
}

// possible calendar drilldowns
func (g *get_app_data_) PossibleCalendarDrillDowns(node *CoreNodeItem, calendar_can_select *CoreDateFacet) {
	calendar_can_select.Month[node.Modified.Month] += 1
	calendar_can_select.Day[node.Modified.Day] += 1
	calendar_can_select.Year[node.Modified.Year] += 1

}

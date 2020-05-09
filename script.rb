require 'csv'
require 'pry'
class CSVConverter
    attr_accessor :facilities_csv, :inventory_csv
    def initialize(facility_file, inventory_file)
        #convert csv's to hashes
        facilities_array = []
        inventory_array = []
        CSV.foreach(facility_file) do |row|
            facilities_array << {fid: row[0], item_grp_id: row[1], target: row[2].to_i}
        end
        facilities_array.shift
        CSV.foreach(inventory_file) do |row|
            inventory_array << {item_grp_id: row[0], item_id: row[1], ship_u: row[2].to_i, each_per_su: row[3].to_i}
        end
        inventory_array.shift
        @facilities_csv = facilities_array
        @inventory_csv = inventory_array
    end
    def filter_facilities(facility_id)
        self.facilities_csv.select {|facility| facility[:fid] == facility_id}
    end
    def filter_facilities_by_item_grp(item_grp_id)
        self.facilities_csv.select {|facility| facility[:item_grp_id] == item_grp_id}
    end
    def filter_inventory_by_item_grp(item_grp_id)
        self.inventory_csv.select {|inventory| inventory[:item_grp_id] == item_grp_id}
    end
    def sample_algorithm #optimize this
        output_array = []
        item_grps = self.inventory_csv.map {|inventory| inventory[:item_grp_id]}.uniq
        item_grps.each do |item_grp|
            inventory_in_item_grp = self.filter_inventory_by_item_grp(item_grp) 
            facilities_in_item_grp = self.filter_facilities_by_item_grp(item_grp)
            inventory_in_item_grp.each do |inventory|
                i = 0
                while i < facilities_in_item_grp.length
                    facilities_in_item_grp[i][:item_id] = inventory[:item_id]
                    facilities_in_item_grp[i][:ship_u] = 0
                    i += 1
                end
                facilities_at_full_stock = 0
                until inventory[:ship_u] < facilities_in_item_grp.length || facilities_at_full_stock == facilities_in_item_grp.length 
                    i = 0
                    while i < facilities_in_item_grp.length
                        if facilities_in_item_grp[i][:target] > 0
                            facilities_in_item_grp[i][:target] -= inventory[:each_per_su]
                            facilities_in_item_grp[i][:ship_u] += 1
                            inventory[:ship_u] -= 1
                        elsif !facilities_in_item_grp[i][:fully_stocked]
                            facilities_at_full_stock += 1
                            facilities_in_item_grp[i][:fully_stocked] = true
                        end
                        i += 1
                    end
                end
            end
            output_array << facilities_in_item_grp
        end
        CSV.open("output.csv", "wb") do |csv|
            csv << ["item_grp_id", "fid", "item_id", "ship_u", "remaining_target"]
            final_output_array = output_array.flatten.uniq
            final_output_array = final_output_array.select{|array| array[:ship_u] != 0}
            final_output_array.each do |row|
                csv << [row[:item_grp_id], row[:fid], row[:item_id], row[:ship_u], row[:target]]
            end
        end
    end
end
csv_converter = CSVConverter.new(ARGV[0], ARGV[1])
csv_converter.sample_algorithm
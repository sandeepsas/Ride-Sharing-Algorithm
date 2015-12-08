package NYCTripAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.joda.time.DateTimeZone;


public class TripDataGenerator {

	static final Logger LOGGER = Logger.getLogger(TripDataGenerator.class.getName());

	static long non_14 = 0;
	static long non_dist = 0;


	public static void main(String[] args) throws IOException
	{
		PrintStream out = new PrintStream(new FileOutputStream("TripData/LaGPickUps.csv"));
		System.setOut(out);
		DateTimeZone.setDefault(DateTimeZone.forID("EST") );

		//PrintWriter skipped_writer = new PrintWriter("F:/LaGPickUps_skipped_v5.csv");


		for(File idx_file:NYCConstants.csv_files)
		{
			long ctr = 0;
			TripDataGenerator.LOGGER.info("Processing"+idx_file.toString());
			BufferedReader file_reader = new BufferedReader(new FileReader(idx_file));
			String file_line = file_reader.readLine();
			while((file_line=file_reader.readLine())!=null 
					&& file_line.length()!=0)
			{
				ctr++;
				String[] file_line_split = file_line.split(",");

				if(file_line_split.length!=14)
				{
					non_14++;
					//skipped_writer.println(file_line);
					continue;
				}

				boolean isMedallion = FilterFunctions.isMedallion(file_line_split[0]);
				boolean isWeekend = FilterFunctions.isWeekday(file_line_split[5]);

				boolean check1 = isMedallion && isWeekend;

				if(check1)
				{
					try
					{
						double longitudeP = Double.parseDouble(file_line_split[10]);
						double latitudeP = Double.parseDouble(file_line_split[11]);
						double longitudeD = Double.parseDouble(file_line_split[12]);
						double latitudeD = Double.parseDouble(file_line_split[13]);
						boolean isLongitudeP = FilterFunctions.isLongitude(longitudeP);
						boolean isLatitudeP = FilterFunctions.isLatitude(latitudeP);
						boolean isLongitudeD = FilterFunctions.isLongitude(longitudeD);
						boolean isLatitudeD = FilterFunctions.isLatitude(latitudeD);

						boolean check2 = (isLongitudeP && isLatitudeP) && (isLongitudeD && isLatitudeD) ;
						if(check2)
						{
							double travel_dist = FilterFunctions.distFrom(latitudeP,
									longitudeP, latitudeD, longitudeD );
							if(travel_dist < 0.1)
							{
								//Runner.LOGGER.info(ctr+"trip_distance < 0.1 miles, Skipped!");
								non_dist++;
								//skipped_writer.println(file_line);
								continue;
							}
							else
							{
								if(FilterFunctions.inLaG(latitudeP,longitudeP) &&
										FilterFunctions.inLaGRange(latitudeD, longitudeD) &&
										FilterFunctions.inNYBoundingBox(latitudeD, longitudeD))
								{
									System.out.println(file_line);
								}
							}

						}
						else
						{
							//Runner.LOGGER.info(ctr+" Co-ordinates are zero, Skipped!");
							//skipped_writer.println(file_line);
							continue;
						}
					}
					catch (NumberFormatException nfe)
					{
						TripDataGenerator.LOGGER.info(ctr+"Number format exception");
					}
				}
				else
				{
					//skipped_writer.println(file_line);
					continue;
				}

			}

			file_reader.close();
		}
		//skipped_writer.close();
		TripDataGenerator.LOGGER.info(non_14+" Number of No 14 lines Skipped!");
		TripDataGenerator.LOGGER.info(non_dist+" Number of Non distance lines Skipped!");
	}

}

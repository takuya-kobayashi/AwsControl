package ec2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import utils.EnvironmentUtils;

public class Ec2StartAndStop implements RequestHandler<Object, Object> {

    // TODO 休業日(祝祭日)をパラメータで設定するか、自動的に判定する
    private String[] companyHolidays = { "3/20", "5/3", "5/4", "5/5", "7/17", "8/11", "9/18", "10/9", "11/3", "11/23" };

    @Override
    public Object handleRequest(Object input, Context context) {

        Collection<String> targetInstanceIds = getTargetInstanceIds();

        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).build();

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("JST"));
        cal.setTime(new Date());
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);

        boolean isNonLaunch = isWeekend(cal);

        if (!isNonLaunch) {
            // 会社が休みの日かどうかを判定する
            for (String companyHoliday : companyHolidays) {
                String[] array = companyHoliday.split("/");
                int companyHolidayMonth = Integer.valueOf(array[0]);
                int companyHolidayDate = Integer.valueOf(array[1]);

                if (companyHolidayMonth == month && companyHolidayDate == date) {
                    isNonLaunch = true;
                    break;
                }
            }
        }

        // TODO 意図的に起動させたい日をパラメータから判定する
//        // 利用希望設定日に一致する場合は休日にしない
//        if (isRequestedDate(cal)) {
//            isNonLaunch = false;
//        }

        if (isNonLaunch) {
            // 起動対象外日の場合は強制停止
            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(targetInstanceIds);
            ec2.stopInstances(request);
            return null;
        }

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour <= 11) {
            // 午前(7-11時)だったらスタート
            StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(targetInstanceIds);
            ec2.startInstances(request);
        } else {
            // 午後だったらストップ
            StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(targetInstanceIds);
            ec2.stopInstances(request);
        }

        return null;
    }

    public Collection<String> getTargetInstanceIds() {

        //環境変数から対象のインスタンスIDを取得してadd
        String[] params = EnvironmentUtils.getEnvironmentVariable("TARGET_INSTANCE_ID").split(",");

        Collection<String> instanceIds = new ArrayList<String>();

        for (String s: params) {
            instanceIds.add(s);
        }

        return instanceIds;
    }

    public boolean isWeekend(Calendar cal) {

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY) {
            return true;
        }

        return false;
    }

    public boolean isRequestedDate(Calendar cal) {

        String requestedDate = EnvironmentUtils.getEnvironmentVariable("REQUESTED_DATE");

        if (requestedDate.isEmpty()) {
            return false;
        }

        String[] requestedDates = requestedDate.split(",");

        for (String date : requestedDates) {
            // TODO 現在日付と比較

        }

        return false;
    }

}

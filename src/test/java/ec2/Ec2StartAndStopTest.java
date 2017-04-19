package ec2;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import utils.EnvironmentUtils;

public class Ec2StartAndStopTest {

    @Ignore
    public void testHandleRequest() {
        fail("まだ実装されていません");
    }

    @Ignore
    public void test1() {
        String[] paramIds = {"i d1 " ,"i　d2", "id3"};
        String str = String.join(",", paramIds);
        str = str.replaceAll("\\s|　", "");
        System.out.println(str);
    }

    @Test
    public void testGetTargetInstanceIds() {

        // 環境変数に設定する値は id1,id2,id3
        String[] paramIds = {"id1" ,"id2", "id3"};
        EnvironmentUtils.setDebugMode();
        EnvironmentUtils.putDebugEnvironmentVariables("TARGET_INSTANCE_ID", String.join(",", paramIds));

        Ec2StartAndStop ec2 = new Ec2StartAndStop();
        Collection<String> instanceIds = ec2.getTargetInstanceIds();

        for (String s : instanceIds) {
            assertThat(true, is(Arrays.asList(paramIds).contains(s)));
        }
    }

    @Ignore
    public void testIsWeekend() {
        fail("まだ実装されていません");
    }

    @Ignore
    public void testIsRequestedDate() {
        fail("まだ実装されていません");
    }

}

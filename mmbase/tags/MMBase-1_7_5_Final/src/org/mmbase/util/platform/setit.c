#include <jni.h>
#include "setUser.h"

#include <pwd.h>
#include <grp.h>

JNIEXPORT jboolean JNICALL Java_org_mmbase_util_platform_setUser_setUserGroupNative(JNIEnv *env,jobject object,jstring juser,jstring jgroup) {
	const char *user,*group;
	struct passwd *pw=NULL;
	struct group *gr=NULL;
	int gid=0;
	int res=1;
 	user=(*env)->GetStringUTFChars(env, juser, 0);
 	group=(*env)->GetStringUTFChars(env, jgroup, 0);

	if (getuid()==0 && strlen(user)>0) {
		if (!(pw=getpwnam(user))) {
			/* Error */
			/* No passwd entry */
			fprintf(stderr, "setUser: No password entry: %s , %s\n", user,group);
			res=0;
		} else if (initgroups(pw->pw_name,pw->pw_gid)==-1) {
			/* Error */
			/* Can't init groups user */
			fprintf(stderr, "setUser: Can't init groups: %s , %s\n", user,group);
			res=0;
		}
		if (strlen(group)>0) {
			if (!(gr=getgrnam(group))) {
				/* Error */
				/* Can't get group entry */
				fprintf(stderr, "setUser: Can't get group entry: %s , %s\n", user,group);
				res=0;
			}
		}
		if (gr) {
			gid=gr->gr_gid;
		} else {
			gid=pw->pw_gid;
		}
		if (gid) {
			if (setgid(gid)==-1) {
				/* Error */
				/* Can't set parent gid */
				fprintf(stderr, "setUser: Can't set group id: %s , %s\n", user,group);
				res=0;
			}
		}
		if (pw && pw->pw_uid) {
			if (setuid(pw->pw_uid)==-1) {
				/* Error */
				/* Can't set user id */
				fprintf(stderr, "setUser: Can't set user id: %s , %s\n", user,group);
				res=0;
			}
		}
	} else {
		fprintf(stderr, "setUser: Not root: %s , %s\n", user,group);
		res=0;
	}

    (*env)->ReleaseStringUTFChars(env, juser, user);
    (*env)->ReleaseStringUTFChars(env, jgroup, group);

	return ( res );
}


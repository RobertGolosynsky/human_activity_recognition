# To run web-customers or web-moderators with **local** profile:
add to Application configuration in IDEA configurations -Dspring.profiles.active=local (as VM option)

# To package or verify the whole project run:
mvn clean verify -Dspring.profiles.active=testing

# Run docker and expose ports

docker run -p 80:8080 ffriends/web-customers:latest

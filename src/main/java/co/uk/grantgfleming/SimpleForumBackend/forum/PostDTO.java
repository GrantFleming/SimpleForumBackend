package co.uk.grantgfleming.SimpleForumBackend.forum;

import lombok.Data;

@Data
public class PostDTO {

    private Long id;

    private Long forumId;

    private String creator;

    private String title;

    private String body;

    public static PostDTO fromPost(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setForumId(post.getForum().getId());
        postDTO.setCreator(post.getCreator().getAlias());
        postDTO.setTitle(post.getTitle());
        postDTO.setBody(post.getBody());
        return postDTO;
    }

}
